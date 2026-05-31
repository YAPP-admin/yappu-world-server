package co.yappuworld.team.client.application

import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.team.client.dto.request.HistoricalServicesPageRequest
import co.yappuworld.team.client.dto.response.HistoricalServiceDetailResponse
import co.yappuworld.team.client.dto.response.HistoricalServicePageResponse
import co.yappuworld.team.infrastructure.TeamMemberFindService
import co.yappuworld.team.infrastructure.TeamServiceImageFindService
import co.yappuworld.team.infrastructure.TeamServiceFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kotlin.math.min

@Service
class HistoricalServiceService(
    private val teamServiceFindService: TeamServiceFindService,
    private val teamMemberFindService: TeamMemberFindService,
    private val teamServiceImageFindService: TeamServiceImageFindService
) {

    @Transactional(readOnly = true)
    fun getHistoricalServices(
        request: HistoricalServicesPageRequest
    ): CursorPageResponse<HistoricalServicePageResponse, UUID> {
        val services = teamServiceFindService.findHistoricalServices(
            generation = request.generation,
            platform = request.platform,
            lastServiceId = request.lastCursorId,
            limit = request.limit + 1
        )
        val pagedServices = services.take(min(request.limit, services.size))
        val thumbnailImageUrls = teamServiceImageFindService.findThumbnailUrls(pagedServices.map { it.serviceId })
        val data = pagedServices
            .map { HistoricalServicePageResponse.from(it, thumbnailImageUrls[it.serviceId]) }

        return CursorPageResponse(
            data = data,
            lastCursor = data.lastOrNull()?.serviceId,
            limit = request.limit,
            hasNext = services.size > request.limit
        )
    }

    @Transactional(readOnly = true)
    fun getHistoricalServiceDetail(serviceId: UUID): HistoricalServiceDetailResponse {
        val service = teamServiceFindService.findTeamService(serviceId)
        val members = teamMemberFindService.findTeamMembersDetail(service.team.id).filterNotNull()
        val thumbnailImageUrl = teamServiceImageFindService.findThumbnailUrl(service)

        return HistoricalServiceDetailResponse.of(service, members, thumbnailImageUrl)
    }
}
