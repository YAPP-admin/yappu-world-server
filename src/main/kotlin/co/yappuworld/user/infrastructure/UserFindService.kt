package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.ActivityUnitEntity
import co.yappuworld.user.domain.model.UserEntity
import co.yappuworld.user.infrastructure.jpa.UserRepository
import co.yappuworld.user.infrastructure.model.ActivityUnitWithRowNumber
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryWhereStep
import com.linecorp.kotlinjdsl.querymodel.jpql.JpqlQueryable
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class UserFindService(
    private val userRepository: UserRepository,
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext
) {

    fun existsByEmail(email: String): Boolean = userRepository.existsUserByEmail(email)

    fun findByIdOrNull(id: UUID): UserEntity? = userRepository.findByIdOrNull(id)

    fun findByEmailOrNull(email: String): UserEntity? = userRepository.findUserOrNullByEmail(email)

    fun findAllByIdIn(ids: List<UUID>): List<UserEntity> = userRepository.findAllByIdIn(ids)

    fun findUserWithLastActivityUnit(userId: UUID): UserWithLastActivityUnit =
        jpql {
            selectUserWithLastActivityUnit()
                .where(path(UserEntity::getId).equal(userId))
                .orderBy(path(UserEntity::getId).desc())
            getUserWithLastActivityUnit(userId)
        }.let { query ->
            entityManager
                .createQuery(query, context)
                .singleResult
        }

    fun findAllUserWithLastActivityUnit(userIds: Collection<UUID>): List<UserWithLastActivityUnit> =
        userRepository
            .findAll {
                selectUserWithLastActivityUnit()
                    .where(path(UserEntity::getId).`in`(userIds))
                    .orderBy(path(UserEntity::getId).desc())
            }.filterNotNull()

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
