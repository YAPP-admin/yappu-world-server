package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.presentation.dto.request.AdminMinSupportVersionUpdateRequest
import co.yappuworld.operation.presentation.dto.response.AdminMinSupportVersionApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "어드민 설정 API", description = "_")
interface AdminOperationApi {

    @Operation(summary = "플랫폼 별 최소 지원 버전 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "platforms": [
                                                {
                                                    "platform": "iOS",
                                                    "version": "1.0.0"
                                                },
                                                {
                                                    "platform": "Android",
                                                    "version": "1.0.0"
                                                }
                                            ]
                                        },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/admin/v1/operations/minimum-support-versions")
    fun getMinSupportVersion(): ResponseEntity<SuccessResponse<AdminMinSupportVersionApiResponseDto>>

    @Operation(summary = "플랫폼 별 최소 지원 버전 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            )
        ]
    )
    @PutMapping("/admin/v1/operations/minimum-support-versions")
    fun updateMinSupportVersion(
        @RequestBody request: AdminMinSupportVersionUpdateRequest
    ): ResponseEntity<Unit>
}
