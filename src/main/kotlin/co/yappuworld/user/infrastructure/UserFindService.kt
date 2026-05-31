package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.util.PageUtils.filterNotNull
import co.yappuworld.schedule.domain.Attendee
import co.yappuworld.user.client.dto.request.AdminUserPageRequest
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.user.infrastructure.jpa.UserRepository
import co.yappuworld.user.infrastructure.model.ActivityUnitWithRowNumber
import co.yappuworld.user.domain.model.UserWithActivityUnit
import co.yappuworld.user.infrastructure.model.UserPersonProfileHistoryProjection
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryWhereStep
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
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
        userRepository.findByIdOrNull(id)
            ?: throw BusinessException(UserError.USER_NOT_FOUND)

    fun findUserOrNull(email: String): UserEntity? = userRepository.findUserOrNullByEmail(email)

    fun findAllByIdIn(ids: List<UUID>): List<UserEntity> {
        require(ids.isNotEmpty()) { "유저 조회 요청에 들어오는 ID는 최소 하나 이상이어야 합니다." }
        return userRepository.findAllByIdIn(ids)
    }

    fun findUserWithLastActivityUnit(userId: UUID): UserWithLastActivityUnit =
        jpql {
            selectUserWithLastActivityUnit()
                .where(path(UserEntity::getId).equal(userId))
                .orderBy(path(UserEntity::getId).desc())
            getUserWithLastActivityUnit(userId)
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

    fun findAllUserWithLastActivityUnit(userIds: Collection<UUID>): List<UserWithLastActivityUnit> {
        require(userIds.isNotEmpty()) { "유저 조회 요청에 들어오는 ID는 최소 하나 이상이어야 합니다." }
        return userRepository
            .findAll {
                selectUserWithLastActivityUnit()
                    .where(path(UserEntity::getId).`in`(userIds))
                    .orderBy(path(UserEntity::getId).desc())
            }.filterNotNull()
    }

    fun findAllUserWithLastActivityUnit(request: AdminUserPageRequest): Page<UserWithLastActivityUnit> =
        userRepository
            .findPage(request.toPageRequest()) {
                val predicates = buildList {
                    request.name?.let { add(path(UserEntity::name).like("%$it%")) }
                    request.generation?.let { add(path(ActivityUnitWithRowNumber::generation).equal(it)) }
                    request.position?.let { add(path(ActivityUnitWithRowNumber::position).equal(it)) }
                    request.role?.let { add(path(UserEntity::role).equal(it)) }
                }

                selectUserWithLastActivityUnit()
                    .whereAnd(*predicates.toTypedArray())
                    .orderBy(path(UserEntity::getId).desc())
            }.filterNotNull()

    fun findUserWithActivities(userId: UUID): UserWithActivityUnits {
        val result = userRepository
            .findAll(CustomUserDsl) {
                selectFromUserWithActivityUnit()
                    .where(path(UserEntity::getId).equal(userId))
            }.filterNotNull()

        if (result.isNotEmpty()) {
            return result.let { UserWithActivityUnits.of(it) }
        }

        when (userRepository.existsById(userId)) {
            true -> throw BusinessException(UserError.NO_ACTIVITY_UNIT)
            false -> throw BusinessException(UserError.USER_NOT_FOUND)
        }
    }

    fun findActiveUsersOfGeneration(generation: Int): List<UserWithActivityUnit> =
        userRepository
            .findAll(CustomUserDsl) { getActiveUser(generation) }
            .filterNotNull()

    fun findSessionAttendeesOfGeneration(generation: Int): List<Attendee> =
        findActiveUsersOfGeneration(generation)
            .filter { it.canCheckIn() }
            .map { Attendee.from(it, generation) }

    fun findSessionAttendee(
        userId: UUID,
        generation: Int
    ): Attendee {
        val result = userRepository
            .findAll(CustomUserDsl) { getActiveUser(userId, generation) }
            .filterNotNull()

        if (result.size > 1) {
            throw BusinessException(UserError.DUPLICATE_ATTENDEE_ACTIVITY)
        }

        if (result.isEmpty()) {
            throw BusinessException(UserError.USER_NOT_FOUND_WITH_GENERATION_ACTIVITY)
        }

        return Attendee.from(result.single(), generation)
    }

    fun findUserWithActivityUnitOfGeneration(
        userId: UUID,
        generation: Int
    ): UserWithActivityUnit =
        userRepository
            .findAll(CustomUserDsl) { getActiveUser(userId, generation) }
            .singleOrNull()
            ?: throw BusinessException(UserError.USER_NOT_FOUND_WITH_GENERATION_ACTIVITY)

    fun findUserPersonProfileHistories(userId: UUID): List<UserPersonProfileHistoryProjection> =
        userRepository
            .findAll(CustomUserDsl) {
                selectUserPersonProfileHistories()
                    .where(path(ActivityUnitEntity::userId).equal(userId))
            }.filterNotNull()

    private fun Jpql.getUserWithLastActivityUnit(
        userId: UUID? = null
    ): JpqlQueryable<SelectQuery<UserWithLastActivityUnit>> =
        selectUserWithLastActivityUnit().let { select ->
            userId?.let { select.where(path(UserEntity::getId).equal(it)) }
            select.orderBy(path(UserEntity::getId).desc())
        }

    private fun Jpql.selectUserWithLastActivityUnit(): SelectQueryWhereStep<UserWithLastActivityUnit> =
        selectNew<UserWithLastActivityUnit>(
            path(UserEntity::getId),
            path(UserEntity::email),
            path(UserEntity::name),
            path(UserEntity::role),
            path(UserEntity::isActive),
            path(UserEntity::createdAt),
            path(ActivityUnitWithRowNumber::generation),
            path(ActivityUnitWithRowNumber::position),
            path(ActivityUnitWithRowNumber::activityUnitId)
        ).from(
            entity(UserEntity::class),
            innerJoin(getActivityUnitWithRowNumber())
                .on(
                    and(
                        path(UserEntity::getId).equal(path(ActivityUnitWithRowNumber::userId)),
                        path(ActivityUnitWithRowNumber::rowNumber).equal(1)
                    )
                )
        )

    private fun Jpql.getActivityUnitWithRowNumber(): Entity<ActivityUnitWithRowNumber> =
        select<ActivityUnitWithRowNumber>(
            path(ActivityUnitEntity::getId).`as`(expression("activityUnitId")),
            path(ActivityUnitEntity::generation).`as`(expression("generation")),
            path(ActivityUnitEntity::position).`as`(expression("position")),
            path(ActivityUnitEntity::userId).`as`(expression("userId")),
            customExpression(Int::class, "ROW_NUMBER() OVER (PARTITION BY userId ORDER BY generation DESC)")
                .`as`(expression("rowNumber"))
        ).from(entity(ActivityUnitEntity::class))
            .asEntity()
}
