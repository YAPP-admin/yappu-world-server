package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.dto.request.AdminMinSupportVersionUpdateRequest
import co.yappuworld.operation.client.dto.request.AdminOperationLinkUpdateRequest
import co.yappuworld.operation.client.dto.response.AdminMinSupportVersionResponse
import co.yappuworld.operation.client.dto.response.AdminOperationLinksResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
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
                useReturnTypeSchema = true,
                content = [
                    Content(
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
    fun getMinSupportVersion(): ResponseEntity<SuccessResponse<AdminMinSupportVersionResponse>>

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

    @Operation(summary = "운영 링크 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "links": [
                                                {
                                                    "id": "privacyPolicyLink",
                                                    "label": "개인정보 처리방침",
                                                    "value": "https://yapp-workspace.notion.site/fc24f8ba29c34f9eb30eb945c621c1ca?pvs=4"
                                                },
                                                {
                                                    "id": "termsOfServiceLink",
                                                    "label": "이용약관",
                                                    "value": "https://yapp-workspace.notion.site/48f4eb2ffdd94740979e8a3b37ca260d?pvs=4"
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
    @GetMapping("/admin/v1/operations/links")
    fun getOperationLinks(): ResponseEntity<SuccessResponse<AdminOperationLinksResponse>>

    @Operation(summary = "운영 링크 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            )
        ]
    )
    @PutMapping("/admin/v1/operations/links")
    fun updateOperationLinks(
        @RequestBody request: AdminOperationLinkUpdateRequest
    ): ResponseEntity<Unit>
}
