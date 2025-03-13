package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.application.ScheduleAdminService
import co.yappuworld.schedule.presentation.dto.request.AdminSessionDeleteApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.AdminSessionDetailsApiResponseDto
import co.yappuworld.schedule.presentation.dto.request.AdminSessionPageApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.SessionCreateApiRequestDto
import co.yappuworld.schedule.presentation.dto.response.AdminSessionOverviewApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class ScheduleAdminController(
    private val scheduleAdminService: ScheduleAdminService
) : ScheduleAdminApi {

    override fun createSchedule(request: SessionCreateApiRequestDto): ResponseEntity<Unit> {
        val scheduleId = scheduleAdminService.createSchedule(request.toAppRequest())
        return ResponseEntity.created(URI.create("/v1/admin/schedules/$scheduleId")).build()
    }

    override fun getSessions(
        request: AdminSessionPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewApiResponseDto>>> =
        scheduleAdminService.getSessions(request.toAppRequest()).let {
            ResponseEntity.ok(
                SuccessResponse(
                    OffsetPageResponse(
                        data = it.sessions.map { AdminSessionOverviewApiResponseDto(it) },
                        totalCount = it.totalElements,
                        totalPages = it.totalPages,
                        page = request.page,
                        size = request.size
                    )
                )
            )
        }

    override fun getSession(sessionId: UUID): ResponseEntity<SuccessResponse<AdminSessionDetailsApiResponseDto>> =
        scheduleAdminService
            .getSession(sessionId)
            .let { ResponseEntity.ok(SuccessResponse(AdminSessionDetailsApiResponseDto(it))) }

    override fun deleteSession(request: AdminSessionDeleteApiRequestDto): ResponseEntity<Unit> {
        scheduleAdminService.deleteSession(request.id)
        return ResponseEntity.noContent().build()
    }
}
