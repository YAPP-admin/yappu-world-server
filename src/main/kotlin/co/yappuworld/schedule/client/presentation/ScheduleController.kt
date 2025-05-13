package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.schedule.client.application.ScheduleService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.request.SessionQueryParamRequest
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.SessionOverviewResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionAttendanceResponse
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController(
    private val scheduleService: ScheduleService
) : ScheduleApi {

    override fun getSchedules(
        request: SchedulePageRequest,
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<SchedulePageResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getSchedules(
                    request,
                    securityUser.userId,
                    getCurrentDateTimeInKST()
                )
            )
        )

    override fun getSessions(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @Valid @ParameterObject request: SessionQueryParamRequest
    ): ResponseEntity<SuccessResponse<SessionOverviewResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getSessions(securityUser.userId, request, getCurrentDateTimeInKST())
            )
        )

    override fun getUpcomingSessionAttendance(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UpcomingSessionAttendanceResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getUpcomingSessionAttendance(securityUser.userId, getCurrentDateTimeInKST())
            )
        )
}
