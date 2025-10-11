package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.schedule.client.application.ScheduleService
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.request.SessionParamRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.SessionDetailsResponse
import co.yappuworld.schedule.client.dto.response.SessionDetailsResponseV2
import co.yappuworld.schedule.client.dto.response.SessionOverviewResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionResponse
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<ActiveGenerationSessionsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getCurrentGenerationSessions(securityUser.userId, getCurrentDateTimeInKST())
            )
        )

    override fun getSessions(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @Valid @ParameterObject request: SessionParamRequest
    ): ResponseEntity<SuccessResponse<SessionOverviewResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getSessions(securityUser.userId, request, getCurrentDateTimeInKST())
            )
        )

    override fun getUpcomingSessionAttendance(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UpcomingSessionResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.findUpcomingSessionWithAttendance(securityUser.userId, getCurrentDateTimeInKST())
            )
        )

    override fun getSessionDetails(
        securityUser: SecurityUser,
        sessionId: UUID
    ): ResponseEntity<SuccessResponse<SessionDetailsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.findSessionDetails(sessionId, getCurrentDateTimeInKST())
            )
        )

    override fun getSessionDetailsV2(
        securityUser: SecurityUser,
        sessionId: UUID
    ): ResponseEntity<SuccessResponse<SessionDetailsResponseV2>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.findSessionDetailsV2(sessionId, getCurrentDateTimeInKST())
            )
        )
}
