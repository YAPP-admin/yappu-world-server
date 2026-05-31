package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TeamServiceImageRepository : JpaRepository<TeamServiceImageEntity, UUID> {
    fun findByTeamServiceAndIsThumbnailTrue(teamService: TeamServiceEntity): TeamServiceImageEntity?

    fun findByTeamServiceIdInAndIsThumbnailTrue(teamServiceIds: List<UUID>): List<TeamServiceImageEntity>

    fun deleteAllByTeamService(teamService: TeamServiceEntity)
}
