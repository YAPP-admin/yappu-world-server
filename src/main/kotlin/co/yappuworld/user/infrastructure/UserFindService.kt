package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.entity.ActivityUnitEntity
import co.yappuworld.user.domain.entity.UserEntity
import co.yappuworld.user.domain.model.ActivityUnit
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.UserRepository
import co.yappuworld.user.infrastructure.model.ActivityUnitWithRowNumber
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
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

    fun findAllUserWithLastActivityUnit(pageable: Pageable): Page<UserWithLastActivityUnit> =
        userRepository
            .findPage(pageable) {
                selectUserWithLastActivityUnit()
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
            .findAll {
                selectNew<UserWithActivityUnit>(
                    path(UserEntity::getId),
                    path(UserEntity::email),
                    path(UserEntity::name),
                    path(UserEntity::role),
                    path(ActivityUnitEntity::generation),
                    path(ActivityUnitEntity::position)
                ).from(
                    entity(UserEntity::class),
                    innerJoin(ActivityUnitEntity::class)
                        .on(path(UserEntity::getId).equal(path(ActivityUnitEntity::userId)))
                ).where(path(UserEntity::getId).equal(userId))
                    .orderBy(path(ActivityUnitEntity::generation).desc())
            }.filterNotNull()

        if (result.isEmpty()) throw BusinessException(UserError.USER_NOT_FOUND)

        return result.let {
            UserWithActivityUnits(
                userId = it.first().userId,
                email = it.first().email,
                name = it.first().name,
                role = it.first().role,
                activityUnits = it
                    .map { au -> ActivityUnit(au.generation, au.position, au.userId) }
                    .sortedByDescending { au -> au.generation }
            )
        }
    }

    fun findUsersActiveOfGeneration(generation: Int): List<UserWithActivityUnit> =
        userRepository
            .findAll {
                selectNew<UserWithActivityUnit>(
                    path(UserEntity::getId),
                    path(UserEntity::email),
                    path(UserEntity::name),
                    path(UserEntity::role),
                    path(ActivityUnitEntity::generation),
                    path(ActivityUnitEntity::position)
                ).from(
                    entity(ActivityUnitEntity::class),
                    innerJoin(entity(UserEntity::class))
                        .on(
                            and(
                                path(ActivityUnitEntity::userId).equal(path(UserEntity::getId)),
                                path(ActivityUnitEntity::generation).equal(generation)
                            )
                        )
                )
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
