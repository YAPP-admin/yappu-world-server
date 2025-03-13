package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.application.ScheduleAdminService
import co.yappuworld.schedule.presentation.dto.request.AdminSessionPageApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.ScheduleCreateApiRequestDto
import co.yappuworld.schedule.presentation.dto.response.AdminSessionOverviewApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class ScheduleAdminController(
    private val scheduleAdminService: ScheduleAdminService
) : ScheduleAdminApi {

    override fun createSchedule(request: ScheduleCreateApiRequestDto): ResponseEntity<Unit> {
        val scheduleId = scheduleAdminService.createSchedule(request.toAppRequest())
        return ResponseEntity.created(URI.create("/v1/admin/schedules/$scheduleId")).build()
    }

    override fun getSchedule(
        request: AdminSessionPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewApiResponseDto>>> {
        TODO("Not yet implemented")
    }
}
