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
    fun findThumbnail(teamService: TeamServiceEntity): TeamServiceImageEntity? =
        teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(teamService)

    fun findThumbnailUrl(teamService: TeamServiceEntity): String? =
        teamServiceImageRepository.findByTeamServiceAndIsThumbnailTrue(teamService)?.imageUrl

    fun findThumbnailUrls(teamServiceIds: List<UUID>): Map<UUID, String> =
        teamServiceImageRepository
            .findByTeamServiceIdInAndIsThumbnailTrue(teamServiceIds)
            .associateBy({ it.teamService.id }, { it.imageUrl })
}
