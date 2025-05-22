package co.yappuworld.user.infrastructure.jdsl

import co.yappuworld.user.domain.model.UserActivityUnit
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.UserEntity
import co.yappuworld.user.infrastructure.dto.ActivityUnitWithRowNumber
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate

class CustomUserDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomUserDsl> {
        override fun newInstance(): CustomUserDsl = CustomUserDsl()
    }

    fun selectUserActivityUnit() =
        selectNew<UserActivityUnit>(
            path(UserEntity::getId),
            path(UserEntity::createdAt),
            path(UserEntity::email),
            path(UserEntity::name),
            path(UserEntity::role),
            path(UserEntity::isActive),
            path(ActivityUnitEntity::getId),
            path(ActivityUnitEntity::generation),
            path(ActivityUnitEntity::position)
        )

    fun selectFromUserActivityUnitWithInnerJoinOn(predicate: Predicate) =
        selectUserActivityUnit().from(
            entity(UserEntity::class),
            innerJoin(entity(ActivityUnitEntity::class))
                .on(predicate)
        )

    fun selectFromUserLastActivityUnit() =
        selectNew<UserActivityUnit>(
            path(UserEntity::getId),
            path(UserEntity::createdAt),
            path(UserEntity::email),
            path(UserEntity::name),
            path(UserEntity::role),
            path(UserEntity::isActive),
            path(ActivityUnitWithRowNumber::activityUnitId),
            path(ActivityUnitWithRowNumber::generation),
            path(ActivityUnitWithRowNumber::position)
        ).from(
            entity(UserEntity::class),
            innerJoin(getUserActivityUnitWithRowNumber())
                .on(
                    and(
                        path(UserEntity::getId).equal(path(ActivityUnitWithRowNumber::userId)),
                        path(ActivityUnitWithRowNumber::rowNumber).equal(1)
                    )
                )
        )

    private fun getUserActivityUnitWithRowNumber() =
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
