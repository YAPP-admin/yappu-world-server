package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.client.application.ScheduleAdminService
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
    private val scheduleAdminService: ScheduleAdminService
) : ScheduleAdminApi {

    override fun createSchedule(request: AdminSessionCreateRequest): ResponseEntity<Unit> {
        val scheduleId = scheduleAdminService.createSchedule(request)
        return ResponseEntity.created(URI.create("/v1/admin/schedules/$scheduleId")).build()
    }

    override fun getSessions(
        request: AdminSessionPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewResponse>>> =
        ResponseEntity.ok(
            SuccessResponse(scheduleAdminService.getSessions(request))
        )

    override fun getSession(sessionId: UUID): ResponseEntity<SuccessResponse<AdminSessionDetailResponse>> =
        scheduleAdminService
            .getSession(sessionId)
            .let { ResponseEntity.ok(SuccessResponse(it)) }

    override fun deleteSession(request: AdminSessionDeleteRequest): ResponseEntity<Unit> {
        scheduleAdminService.deleteSession(request.id)
        return ResponseEntity.noContent().build()
    }

    override fun updateSession(request: AdminSessionUpdateRequest): ResponseEntity<Unit> {
        scheduleAdminService.updateSession(request)
        return ResponseEntity.noContent().build()
    }
}
