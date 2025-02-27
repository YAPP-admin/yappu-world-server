package co.yappuworld.schedule.presentation

import co.yappuworld.schedule.presentation.dto.request.ScheduleCreateApiRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.util.UUID

@RestController
class ScheduleAdminController : ScheduleAdminApi {

    override fun createSchedule(request: ScheduleCreateApiRequestDto): ResponseEntity<Unit> {
        return ResponseEntity.created(URI.create("/v1/admin/schedules/${UUID.randomUUID()}")).build()
    }
}
