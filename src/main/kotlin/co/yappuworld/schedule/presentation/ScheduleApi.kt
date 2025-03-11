package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.presentation.dto.response.ActiveGenerationSessionsApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
}
