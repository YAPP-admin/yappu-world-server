package co.yappuworld.team.client.application

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.team.client.dto.request.AdminTeamServiceCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamServicePageRequest
import co.yappuworld.team.client.dto.request.AdminTeamServiceUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamServiceDetailResponse
import co.yappuworld.team.client.dto.response.AdminTeamServiceResponse
import co.yappuworld.team.infrastructure.TeamFindService
import co.yappuworld.team.infrastructure.TeamServiceImageCommandService
import co.yappuworld.team.infrastructure.TeamServiceImageFindService
import co.yappuworld.team.infrastructure.TeamServiceCommandService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import co.yappuworld.team.infrastructure.entity.ServiceLinks
import co.yappuworld.team.infrastructure.entity.TeamServiceEntity
import co.yappuworld.team.infrastructure.entity.TeamServiceImageEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class AdminTeamServiceManageService(
    private val teamFindService: TeamFindService,
    private val teamServiceFindService: TeamServiceFindService,
    private val teamServiceCommandService: TeamServiceCommandService,
    private val teamServiceImageFindService: TeamServiceImageFindService,
    private val teamServiceImageCommandService: TeamServiceImageCommandService
) {

    @Transactional(readOnly = true)
    fun getTeamServices(request: AdminTeamServicePageRequest): OffsetPageResponse<AdminTeamServiceResponse> {
        val servicesPage = teamServiceFindService.findTeamServices(
            request.generation,
            request.toPageRequest()
        )
        return OffsetPageResponse(
            data = servicesPage.content.map { AdminTeamServiceResponse.from(it) },
            totalCount = servicesPage.totalElements,
            totalPages = servicesPage.totalPages,
            page = request.page,
            size = request.size
        )
    }

    @Transactional(readOnly = true)
    fun getTeamService(serviceId: UUID): AdminTeamServiceDetailResponse {
        val service = teamServiceFindService.findTeamService(serviceId)
        val thumbnailImageUrl = teamServiceImageFindService.findThumbnailUrl(service)
        return AdminTeamServiceDetailResponse.from(service, thumbnailImageUrl)
    }

    @Transactional
    fun createTeamService(request: AdminTeamServiceCreateRequest): UUID {
        val team = teamFindService.findTeam(request.teamId)
        val service = request.toService(team).also { teamServiceCommandService.save(it) }
        createThumbnail(service, request.thumbnailImageUrl?.trim()?.takeIf { it.isNotEmpty() })
        return service.id
    }

    @Transactional
    fun updateTeamService(request: AdminTeamServiceUpdateRequest) {
        val service = teamServiceFindService.findTeamService(request.id)
        val team = teamFindService.findTeam(request.teamId)
        service.update(
            team = team,
            name = request.name,
            hasApp = request.hasApp,
            hasWeb = request.hasWeb,
            serviceLinks = ServiceLinks(
                googlePlay = request.googlePlayLink,
                appStore = request.appStoreLink,
                web = request.webLink
            ),
            summary = request.summary,
            description = request.description,
            isOperating = request.isOperating
        )
        updateThumbnail(service, request.thumbnailImageUrl?.trim()?.takeIf { it.isNotEmpty() })
    }

    @Transactional
    fun deleteTeamService(serviceId: UUID) {
        val service = teamServiceFindService.findTeamService(serviceId)
        teamServiceImageCommandService.deleteAll(service)
        teamServiceCommandService.delete(service)
    }

    private fun createThumbnail(
        service: TeamServiceEntity,
        thumbnailImageUrl: String?
    ) {
        thumbnailImageUrl?.let {
            teamServiceImageCommandService.save(
                TeamServiceImageEntity(
                    teamService = service,
                    imageUrl = it,
                    isThumbnail = true
                )
            )
        }
    }

    private fun updateThumbnail(
        service: TeamServiceEntity,
        thumbnailImageUrl: String?
    ) {
        val thumbnail = teamServiceImageFindService.findThumbnail(service)

        thumbnailImageUrl
            ?.let { thumbnail?.updateImageUrl(it) ?: createThumbnail(service, it) }
            ?: thumbnail?.let { teamServiceImageCommandService.delete(it) }
    }
}
