package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamEntity
import co.yappuworld.team.infrastructure.entity.TeamMemberEntity
import co.yappuworld.user.infrastructure.entity.ActivityUnitEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TeamMemberRepository :
    JpaRepository<TeamMemberEntity, UUID>,
    KotlinJdslJpqlExecutor {
    fun findByTeam(team: TeamEntity): List<TeamMemberEntity>

    fun findByActivityUnit(activityUnit: ActivityUnitEntity): TeamMemberEntity?

    fun deleteAllByTeam(team: TeamEntity)
}
