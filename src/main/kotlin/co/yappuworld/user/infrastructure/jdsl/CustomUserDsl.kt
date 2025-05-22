package co.yappuworld.user.infrastructure.jdsl

import co.yappuworld.user.domain.model.UserActivityUnit
import co.yappuworld.user.infrastructure.jpa.ActivityUnitEntity
import co.yappuworld.user.infrastructure.jpa.UserEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate

class CustomUserDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomUserDsl> {
        override fun newInstance(): CustomUserDsl = CustomUserDsl()
    }

    fun selectFromUserActivityUnitWithInnerJoinOn(predicate: Predicate) =
        selectNew<UserActivityUnit>(
            path(UserEntity::getId),
            path(UserEntity::email),
            path(UserEntity::name),
            path(UserEntity::role),
            path(ActivityUnitEntity::getId),
            path(ActivityUnitEntity::generation),
            path(ActivityUnitEntity::position)
        ).from(
            entity(UserEntity::class),
            innerJoin(entity(ActivityUnitEntity::class))
                .on(predicate)
        )
}
