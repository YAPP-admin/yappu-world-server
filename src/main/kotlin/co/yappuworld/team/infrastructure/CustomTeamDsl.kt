package co.yappuworld.team.infrastructure

import co.yappuworld.team.domain.vo.ServicePlatform
import co.yappuworld.team.infrastructure.entity.ServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamEntity
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.sort.Sortable
import org.springframework.stereotype.Component

@Component
class CustomTeamDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CustomTeamDsl> {
        override fun newInstance(): CustomTeamDsl = CustomTeamDsl()
    }

    fun teamSorting(platform: ServicePlatform?): List<Sortable> =
        listOfNotNull(
            path(TeamEntity::generation).desc(),
            if (platform == null) {
                caseWhen(path(TeamEntity::service)(ServiceEntity::hasApp).equal(true))
                    .then(1)
                    .`else`(2)
                    .asc()
            } else {
                null
            },
            path(TeamEntity::name).desc()
        )
}
