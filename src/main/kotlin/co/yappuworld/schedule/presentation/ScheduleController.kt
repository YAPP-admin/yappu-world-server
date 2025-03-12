package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.util.TimeUtils
import co.yappuworld.schedule.application.ScheduleService
import co.yappuworld.schedule.presentation.dto.request.SchedulePageApiRequestDto
import co.yappuworld.schedule.presentation.dto.response.ActiveGenerationSessionsApiResponseDto
import co.yappuworld.schedule.presentation.dto.response.SchedulePageApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class ScheduleController(
    private val scheduleService: ScheduleService
) : ScheduleApi {

    override fun getSessions(): ResponseEntity<SuccessResponse<ActiveGenerationSessionsApiResponseDto>> {
        val now = TimeUtils.getCurrentDateTimeInKST()
        return ResponseEntity.ok(
            SuccessResponse(
                ActiveGenerationSessionsApiResponseDto(
                    scheduleService.getCurrentGenerationSessions(now.toLocalDate())
                )
            )
        )
    }

    override fun getSchedules(
        request: SchedulePageApiRequestDto
    ): ResponseEntity<SuccessResponse<SchedulePageApiResponseDto>> {
        val now = TimeUtils.getCurrentDateTimeInKST()
        return ResponseEntity.ok(
            SuccessResponse(
                SchedulePageApiResponseDto(scheduleService.getSchedules(request.toAppRequestDto(), now))
            )
        )
    }
}
