package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.util.DatetimeUtils
import co.yappuworld.schedule.client.application.ScheduleService
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponseV2
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ActiveGenerationSessionController(
    private val scheduleService: ScheduleService
) : ActiveGenerationSessionApi {

    override fun getActiveGenerationSessions(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<ActiveGenerationSessionsResponseV2>> =
        ResponseEntity.ok(
            SuccessResponse(
                scheduleService.getActiveGenerationSessions(
                    securityUser.userId,
                    DatetimeUtils.getCurrentDateTimeInKST()
                )
            )
        )
}
