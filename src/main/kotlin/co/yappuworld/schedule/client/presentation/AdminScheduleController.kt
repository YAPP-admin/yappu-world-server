package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.client.application.AdminScheduleService
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionEligibleUsersParamRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionEligibleUsersResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class AdminScheduleController(
    private val adminScheduleService: AdminScheduleService
) : AdminScheduleApi {

    override fun createSession(request: AdminSessionCreateRequest): ResponseEntity<Unit> {
        val scheduleId = adminScheduleService.createSchedule(request)
        return ResponseEntity.created(URI.create("/v1/admin/schedules/$scheduleId")).build()
    }

    override fun getSessions(
        request: AdminSessionPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(adminScheduleService.getSessions(request))
        )

    override fun getSession(sessionId: UUID): ResponseEntity<SuccessResponse<AdminSessionDetailResponse>> =
        adminScheduleService
            .getSession(sessionId)
            .let { ResponseEntity.ok(SuccessResponse(it)) }

    override fun deleteSessions(request: AdminSessionDeleteRequest): ResponseEntity<Unit> {
        adminScheduleService.deleteSessions(request)
        return ResponseEntity.noContent().build()
    }

    override fun updateSession(request: AdminSessionUpdateRequest): ResponseEntity<Unit> {
        adminScheduleService.updateSession(request)
        return ResponseEntity.noContent().build()
    }

    override fun getSessionEligibleUsers(
        request: AdminSessionEligibleUsersParamRequest
    ): ResponseEntity<SuccessResponse<AdminSessionEligibleUsersResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminScheduleService.getSessionEligibleUsers(request))
        )
}
