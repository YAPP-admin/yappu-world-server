package co.yappuworld.team.client.presentation

import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.team.client.application.HistoricalServiceService
import co.yappuworld.team.client.dto.request.HistoricalServicesPageRequest
import co.yappuworld.team.client.dto.response.HistoricalServiceDetailResponse
import co.yappuworld.team.client.dto.response.HistoricalServicePageResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class HistoricalServiceController(
    private val historicalServiceService: HistoricalServiceService
) : HistoricalServiceApi {

    override fun getHistoricalServices(
        request: HistoricalServicesPageRequest
    ): ResponseEntity<SuccessResponse<CursorPageResponse<HistoricalServicePageResponse, UUID>>> =
        ResponseEntity.ok(
            SuccessResponse(historicalServiceService.getHistoricalServices(request))
        )

    override fun getHistoricalServiceDetail(
        serviceId: UUID
    ): ResponseEntity<SuccessResponse<HistoricalServiceDetailResponse>> =
        ResponseEntity.ok(
            SuccessResponse(historicalServiceService.getHistoricalServiceDetail(serviceId))
        )
}
