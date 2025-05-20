package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.client.application.AdminScheduleService
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class ScheduleAdminController(
    private val adminScheduleService: AdminScheduleService
) : ScheduleAdminApi {

    override fun createSchedule(request: AdminSessionCreateRequest): ResponseEntity<Unit> {
        val scheduleId = adminScheduleService.createSession(request)
        return ResponseEntity.created(URI.create("/v1/admin/schedules/$scheduleId")).build()
    }

    override fun getSessions(
        request: AdminSessionPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(adminScheduleService.getSessions(request))
        )

    override fun getSession(sessionId: UUID): ResponseEntity<SuccessResponse<AdminSessionDetailResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminScheduleService.getSession(sessionId))
        )

    override fun deleteSession(request: AdminSessionDeleteRequest): ResponseEntity<Unit> {
        adminScheduleService.deleteSession(request)
        return ResponseEntity.noContent().build()
    }

    override fun updateSession(request: AdminSessionUpdateRequest): ResponseEntity<Unit> {
        adminScheduleService.updateSession(request)
        return ResponseEntity.noContent().build()
    }
}
