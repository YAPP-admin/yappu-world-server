package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TeamServiceRepository :
    JpaRepository<TeamServiceEntity, UUID>,
    KotlinJdslJpqlExecutor {
    fun findByTeam(team: TeamEntity): List<TeamServiceEntity>

    fun deleteAllByTeam(team: TeamEntity)

    fun findByTeamIn(teams: List<TeamEntity>): List<TeamServiceEntity>
}
