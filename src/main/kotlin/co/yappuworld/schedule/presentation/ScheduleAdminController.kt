package co.yappuworld.schedule.presentation

import co.yappuworld.schedule.application.ScheduleAdminService
import co.yappuworld.schedule.presentation.dto.request.ScheduleCreateApiRequestDto
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
}
