package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.presentation.dto.response.AdminForceUpdateInfoApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "어드민 설정 API", description = "_")
interface AdminOperationApi {

    @Operation(summary = "강제 업데이트 정보 조회")
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
    @GetMapping("/admin/v1/operations/force-updates")
    fun getForceUpdateInfo(): ResponseEntity<SuccessResponse<AdminForceUpdateInfoApiResponseDto>>
}
