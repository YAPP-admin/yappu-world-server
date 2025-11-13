package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.dto.TeamWithServiceDto
import co.yappuworld.team.domain.vo.ServicePlatform
import co.yappuworld.team.infrastructure.dto.TeamMemberDetailDto
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.select.SelectQueryFromStep
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sortable
import org.springframework.stereotype.Component

@Component
class
CustomTeamDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomTeamDsl> {
        override fun newInstance(): CustomTeamDsl = CustomTeamDsl()
    }

    fun teamSorting(platform: ServicePlatform?): List<Sortable> =
        buildList {
            add(path(TeamEntity::generation).desc())
            if (platform == null) {
                add(
                    caseWhen(path(TeamServiceEntity::hasApp).equal(true))
                        .then(1)
                        .`else`(2)
                        .asc()
                )
            }
            add(path(TeamEntity::name).desc())
        }

    fun selectTeamWithService(): SelectQueryFromStep<TeamWithServiceDto> =
        selectNew<TeamWithServiceDto>(
            path(TeamEntity::getId),
            path(TeamEntity::name),
            path(TeamEntity::generation),
            path(TeamServiceEntity::getId),
            path(TeamServiceEntity::name),
            path(TeamServiceEntity::hasApp),
            path(TeamServiceEntity::hasWeb),
            path(TeamServiceEntity::serviceLinks)
        )

    fun selectTeamMemberDetail(): SelectQueryFromStep<TeamMemberDetailDto> =
        selectNew<TeamMemberDetailDto>(
            path(TeamMemberEntity::activityUnit).path(ActivityUnitEntity::getId),
            path(TeamMemberEntity::activityUnit).path(ActivityUnitEntity::generation),
            path(TeamMemberEntity::activityUnit).path(ActivityUnitEntity::position),
            path(UserEntity::name)
        )
}
