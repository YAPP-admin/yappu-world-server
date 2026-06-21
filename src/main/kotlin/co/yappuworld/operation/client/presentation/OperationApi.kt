package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.dto.request.ForceUpdateInquiryRequest
import co.yappuworld.operation.client.dto.response.ActiveGenerationResponse
import co.yappuworld.operation.client.dto.response.ForceUpdateResponse
import co.yappuworld.operation.client.dto.response.GenerationsResponse
import co.yappuworld.operation.client.dto.response.OperationLinkResponse
import co.yappuworld.operation.client.dto.response.PositionsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "운영 API", description = "_")
interface OperationApi {

    @Operation(summary = "직군 정보")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "직군 정보 조회 성공",
                                value = """
                                    {
                                        "data": {
                                            "positions": [
                                                {
                                                    "name": "PM",
                                                    "label": "PM"
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
    @GetMapping("/v1/operations/positions")
    fun getPositions(): ResponseEntity<SuccessResponse<PositionsResponse>>

    @Operation(summary = "역대 기수 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "역대 기수 목록 조회 성공",
                                value = """
                                    {
                                        "data": {
                                            "generations": [
                                                {
                                                    "generation": 25,
                                                    "startDate": "2024-05-03",
                                                    "endDate": "2024-09-14",
                                                    "isActive": true
                                                },
                                                {
                                                    "generation": 24,
                                                    "startDate": "2023-11-11",
                                                    "endDate": "2024-04-20",
                                                    "isActive": false
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
    @GetMapping("/v1/operations/generations")
    fun getGenerations(): ResponseEntity<SuccessResponse<GenerationsResponse>>

    @Operation(summary = "강제 업데이트 정보")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = ForceUpdateResponse::class),
                        examples = [
                            ExampleObject(
                                name = "강제 업데이트 필요",
                                //                                ref = "#/components/schemas/SuccessResponseForceUpdateResponse",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "needForceUpdate": "true"
                                        }
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "강제 업데이트 불필요",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "needForceUpdate": "false"
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                description = "잘못된 포맷 요청",
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "강제 업데이트 필요",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "message": "올바르지 않은 버전 형태입니다. 버전은 x.y.z 형태여야 합니다.",
                                        "errorCode": "CFG_0001"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/operations/force-update")
    fun getForceUpdateInfo(
        @ParameterObject request: ForceUpdateInquiryRequest
    ): ResponseEntity<SuccessResponse<ForceUpdateResponse>>

    @Operation(summary = "현재 활동 중인 기수")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "활동 중일 때",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "isActive": true,
                                            "generation": 23
                                        }
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "활동 중이 아닐 때",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "isActive": false,
                                            "generation": null
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/operations/active-generation")
    fun getActiveGeneration(): ResponseEntity<SuccessResponse<ActiveGenerationResponse>>

    @Operation(summary = "이용 문의 링크")
    @GetMapping("/v1/operations/links/usage-inquiry")
    fun getUsageInquiryLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>>

    @Operation(summary = "이용 약관 링크")
    @GetMapping("/v1/operations/links/terms-of-service")
    fun getTermsOfServiceLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>>

    @Operation(summary = "개인정보 처리방침 링크")
    @GetMapping("/v1/operations/links/privacy-policy")
    fun getPrivacyPolicyLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>>

    @Operation(summary = "기본 규칙 링크")
    @GetMapping("/v1/operations/links/basic-rule")
    fun getBasicRuleLink(): ResponseEntity<SuccessResponse<OperationLinkResponse>>
}
