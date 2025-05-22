package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.Attendee
import co.yappuworld.user.domain.model.UserActivityUnit
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jdsl.CustomUserDsl
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.UserEntity
import co.yappuworld.user.infrastructure.jpa.UserRepository
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val logger = KotlinLogging.logger {}

@Service
@Transactional(readOnly = true)
class UserFindService(
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext
) {

    fun existsEmail(email: String): Boolean = userRepository.existsUserByEmail(email)

    fun findUserOrNull(id: UUID): UserEntity? = userRepository.findByIdOrNull(id)

    fun findUser(id: UUID): UserEntity =
        userRepository.findByIdOrNull(id) ?: throw BusinessException(UserError.USER_NOT_FOUND)

    fun findUserOrNull(email: String): UserEntity? = userRepository.findUserOrNullByEmail(email)

    fun findAllByIdIn(ids: List<UUID>): List<UserEntity> {
        require(ids.isNotEmpty()) { "유저 조회 요청에 들어오는 ID는 최소 하나 이상이어야 합니다." }
        return userRepository.findAllByIdIn(ids)
    }

    fun findUserLastActivityUnit(userId: UUID): UserActivityUnit =
        jpql(CustomUserDsl) {
            selectFromUserLastActivityUnit()
                .where(path(UserEntity::getId).equal(userId))
                .orderBy(path(UserEntity::getId).desc())
        }.let { query ->
            val typedQuery = entityManager.createQuery(query, context)
            when (typedQuery.resultList.size) {
                0 -> throw BusinessException(UserError.USER_NOT_FOUND)
                1 -> typedQuery.singleResult
                else -> {
                    logger.error { "${typedQuery.resultList.size}개의 결과가 나오면 안 됩니다." }
                    throw BusinessException(UserError.USER_FIND_ERROR)
                }
            }
        }

    fun findAllUserLastActivityUnit(userIds: Collection<UUID>): List<UserActivityUnit> {
        require(userIds.isNotEmpty()) { "유저 조회 요청에 들어오는 ID는 최소 하나 이상이어야 합니다." }
        return userRepository
            .findAll(CustomUserDsl) {
                selectFromUserLastActivityUnit()
                    .where(path(UserEntity::getId).`in`(userIds))
                    .orderBy(path(UserEntity::getId).desc())
            }.filterNotNull()
    }

    fun findAllUserLastActivityUnit(pageable: Pageable): Page<UserActivityUnit> =
        userRepository
            .findPage(CustomUserDsl, pageable) {
                selectFromUserLastActivityUnit()
                    .orderBy(path(UserEntity::getId).desc())
            }.let {
                PageImpl(
                    it.content.filterNotNull(),
                    it.pageable,
                    it.totalElements
                )
            }

    fun findUserWithActivities(userId: UUID): UserWithActivityUnits {
        val result = userRepository
            .findAll(CustomUserDsl) {
                selectUserActivityUnit().from(
                    entity(UserEntity::class),
                    innerJoin(ActivityUnitEntity::class)
                        .on(
                            and(
                                path(UserEntity::getId).equal(path(ActivityUnitEntity::userId)),
                                path(UserEntity::getId).equal(userId)
                            )
                        )
                )
            }.filterNotNull()

        if (result.isNotEmpty()) {
            return result.let { UserWithActivityUnits.of(it) }
        }

        when (userRepository.existsById(userId)) {
            true -> throw BusinessException(UserError.NO_ACTIVITY_UNIT)
            false -> throw BusinessException(UserError.USER_NOT_FOUND)
        }
    }

    fun findSessionAttendee(
        userId: UUID,
        generation: Int
    ): Attendee {
        val result = userRepository
            .findAll(CustomUserDsl) {
                selectUserActivityUnit()
                    .from(
                        entity(UserEntity::class),
                        innerJoin(entity(ActivityUnitEntity::class))
                            .on(
                                and(
                                    path(UserEntity::getId).equal(path(ActivityUnitEntity::userId)),
                                    path(UserEntity::getId).equal(userId),
                                    path(ActivityUnitEntity::generation).equal(generation),
                                    path(ActivityUnitEntity::position).notEqual(Position.STAFF)
                                )
                            )
                    )
            }.filterNotNull()

        if (result.size > 1) {
            throw BusinessException(UserError.DUPLICATE_ATTENDEE_ACTIVITY)
        }

        if (result.isEmpty()) {
            throw BusinessException(UserError.USER_NOT_FOUND_WITH_GENERATION_ACTIVITY)
        }

        return Attendee.from(result.single(), generation)
    }

    fun findAllUserActivityUnitOfGeneration(generation: Int): List<UserActivityUnit> =
        userRepository
            .findAll(CustomUserDsl) {
                selectFromUserActivityUnitWithInnerJoinOn(
                    and(
                        path(UserEntity::getId).equal(path(ActivityUnitEntity::userId)),
                        path(ActivityUnitEntity::generation).equal(generation)
                    )
                )
            }.filterNotNull()
}
