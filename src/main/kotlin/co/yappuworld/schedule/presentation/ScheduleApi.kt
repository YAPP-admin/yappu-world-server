package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.presentation.dto.request.SchedulePageApiRequestDto
import co.yappuworld.schedule.presentation.dto.response.ActiveGenerationSessionsApiResponseDto
import co.yappuworld.schedule.presentation.dto.response.SchedulePageApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "스케줄 API", description = "스케줄 목록")
interface ScheduleApi {

    @Operation(summary = "활동 중인 기수의 세션 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/v1/sessions")
    fun getSessions(): ResponseEntity<SuccessResponse<ActiveGenerationSessionsApiResponseDto>>

    @Operation(summary = "일정 조회")
    @ApiResponses()
    @GetMapping("/v1/schedules")
    fun getSchedules(
        @Valid @ParameterObject request: SchedulePageApiRequestDto
    ): ResponseEntity<SuccessResponse<SchedulePageApiResponseDto>>
}
