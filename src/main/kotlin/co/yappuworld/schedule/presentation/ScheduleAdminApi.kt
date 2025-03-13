package co.yappuworld.schedule.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.presentation.dto.request.AdminSessionPageApiRequestDto
import co.yappuworld.schedule.presentation.dto.request.ScheduleCreateApiRequestDto
import co.yappuworld.schedule.presentation.dto.response.AdminSessionOverviewApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "스케줄 어드민 API", description = "스케줄 생성, 수정, 삭제 등 for Admin")
interface ScheduleAdminApi {

    @Operation(summary = "스케줄 생성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                content = [Content()]
            )
        ]
    )
    @PostMapping("/admin/v1/schedules")
    fun createSchedule(
        @Valid @RequestBody request: ScheduleCreateApiRequestDto
    ): ResponseEntity<Unit>

    @Operation(summary = "스케줄 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [Content()]
            )
        ]
    )
    @GetMapping("/admin/v1/sessions")
    fun getSessions(
        @Valid @ParameterObject request: AdminSessionPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewApiResponseDto>>>
}
