package co.yappuworld.user.infrastructure

import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.user.domain.model.UserWithActivityUnit
import co.yappuworld.user.infrastructure.model.UserPersonProfileHistoryProjection
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

    fun selectUserPersonProfileHistories(): SelectQueryWhereStep<UserPersonProfileHistoryProjection> =
        selectNew<UserPersonProfileHistoryProjection>(
            path(ActivityUnitEntity::getId),
            path(ActivityUnitEntity::generation),
            path(ActivityUnitEntity::position),
            path(GenerationEntity::startDate),
            path(GenerationEntity::endDate),
            path(TeamEntity::name),
            path(TeamServiceEntity::getId),
            path(TeamServiceEntity::name),
            path(TeamServiceEntity::summary),
            path(TeamServiceEntity::hasApp),
            path(TeamServiceEntity::hasWeb)
        ).from(
            entity(ActivityUnitEntity::class),
            leftJoin(entity(GenerationEntity::class))
                .on(path(ActivityUnitEntity::generation).equal(path(GenerationEntity::value))),
            leftJoin(entity(TeamMemberEntity::class))
                .on(
                    path(ActivityUnitEntity::getId)
                        .equal(path(TeamMemberEntity::activityUnit).path(ActivityUnitEntity::getId))
                ),
            leftJoin(entity(TeamEntity::class))
                .on(path(TeamMemberEntity::team).path(TeamEntity::getId).equal(path(TeamEntity::getId))),
            leftJoin(entity(TeamServiceEntity::class))
                .on(path(TeamEntity::getId).equal(path(TeamServiceEntity::team).path(TeamEntity::getId)))
        )
}
