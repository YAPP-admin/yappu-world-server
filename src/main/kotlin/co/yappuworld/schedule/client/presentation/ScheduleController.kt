package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.util.TimeUtils
import co.yappuworld.schedule.client.application.ScheduleService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionAttendanceResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController(
    private val scheduleService: ScheduleService
) : ScheduleApi {

    override fun getSessions(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<ActiveGenerationSessionsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getCurrentGenerationSessions(securityUser.userId, TimeUtils.getCurrentDateTimeInKST())
            )
        )

    override fun getSchedules(request: SchedulePageRequest): ResponseEntity<SuccessResponse<SchedulePageResponse>> {
        val now = TimeUtils.getCurrentDateTimeInKST()
        return ResponseEntity.ok(
            SuccessResponse(scheduleService.getSchedules(request, now))
        )
    }

    override fun getUpcomingSessionAttendance(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UpcomingSessionAttendanceResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getUpcomingSessionAttendance(securityUser.userId, TimeUtils.getCurrentDateTimeInKST())
            )
        )
}
