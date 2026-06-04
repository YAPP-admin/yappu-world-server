package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceImageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface TeamServiceImageRepository : JpaRepository<TeamServiceImageEntity, UUID> {
    fun findByTeamService(teamService: TeamServiceEntity): List<TeamServiceImageEntity>

    fun findByTeamServiceAndIsThumbnailTrue(teamService: TeamServiceEntity): TeamServiceImageEntity?

    @Query(
        """
        SELECT tsi.teamService.id AS teamServiceId, tsi.objectKey AS objectKey
        FROM TeamServiceImageEntity tsi
        WHERE tsi.teamService.id IN :teamServiceIds
            AND tsi.isThumbnail = true
        """
    )
    fun findThumbnailObjectKeysByTeamServiceIdIn(teamServiceIds: List<UUID>): List<TeamServiceImageObjectKey>

    fun deleteAllByTeamService(teamService: TeamServiceEntity)
}

interface TeamServiceImageObjectKey {
    val teamServiceId: UUID
    val objectKey: String
}
