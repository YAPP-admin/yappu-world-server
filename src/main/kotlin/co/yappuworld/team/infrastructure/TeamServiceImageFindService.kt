package co.yappuworld.team.infrastructure

import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceImageEntity
import co.yappuworld.team.infrastructure.jpa.TeamServiceImageRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional(readOnly = true)
class TeamServiceImageFindService(
    private val teamServiceImageRepository: TeamServiceImageRepository
) {
    fun findImages(teamService: TeamServiceEntity): List<TeamServiceImageEntity> =
        teamServiceImageRepository.findByTeamService(teamService)

    fun findThumbnail(teamService: TeamServiceEntity): TeamServiceImageEntity? =
        teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(teamService)

    fun findThumbnailObjectKey(teamService: TeamServiceEntity): String? =
        teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(teamService)?.objectKey

    fun findThumbnailObjectKeys(teamServiceIds: List<UUID>): Map<UUID, String> =
        teamServiceImageRepository
            .findThumbnailObjectKeysByTeamServiceIdIn(teamServiceIds)
            .associateBy({ it.teamServiceId }, { it.objectKey })
}
