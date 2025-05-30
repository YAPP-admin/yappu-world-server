package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryGroupByStep
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryWhereStep
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class CustomUserDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomUserDsl> {
        override fun newInstance(): CustomUserDsl = CustomUserDsl()
    }

    fun selectFromUserWithActivityUnit(): SelectQueryWhereStep<UserWithActivityUnit> =
        selectNew<UserWithActivityUnit>(
            path(UserEntity::getId),
            path(UserEntity::email),
            path(UserEntity::name),
            path(UserEntity::role),
            path(ActivityUnitEntity::getId),
            path(ActivityUnitEntity::generation),
            path(ActivityUnitEntity::position)
        ).from(
            entity(ActivityUnitEntity::class),
            innerJoin(entity(UserEntity::class))
                .on(path(ActivityUnitEntity::userId).equal(path(UserEntity::getId)))
        )

    fun getActiveUser(generation: Int): SelectQueryGroupByStep<UserWithActivityUnit> =
        selectFromUserWithActivityUnit()
            .whereAnd(
                path(ActivityUnitEntity::generation).equal(generation),
                path(ActivityUnitEntity::position).notEqual(Position.STAFF)
            )

    fun getActiveUser(
        userId: UUID,
        generation: Int
    ): SelectQueryGroupByStep<UserWithActivityUnit> =
        selectFromUserWithActivityUnit()
            .whereAnd(
                path(UserEntity::getId).equal(userId),
                path(ActivityUnitEntity::generation).equal(generation),
                path(ActivityUnitEntity::position).notEqual(Position.STAFF)
            )
}
