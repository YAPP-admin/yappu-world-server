package co.yappuworld.operation.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.presentation.dto.response.ActiveGenerationApiResponseDto
import co.yappuworld.operation.presentation.dto.response.ForceUpdateApiResponseDto
import co.yappuworld.operation.presentation.dto.response.PositionsApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "운영 API")
interface OperationApi {

    @Operation(summary = "직군 정보")
    @GetMapping("/v1/operations/positions")
    fun getPositions(): ResponseEntity<SuccessResponse<PositionsApiResponseDto>>

    @Operation(summary = "강제 업데이트 정보")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "강제 업데이트 필요",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "needForceUpdate": "true",
                                            "reason": "강제 업데이트 하라면 하쇼"
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
                                            "needForceUpdate": "false",
                                            "reason": null
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
    @GetMapping("/v1/operations/force-update")
    fun getForceUpdateInfo(): ResponseEntity<SuccessResponse<ForceUpdateApiResponseDto>>

    @Operation(summary = "현재 활동 중인 기수")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
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
    fun getActiveGeneration(): ResponseEntity<SuccessResponse<ActiveGenerationApiResponseDto>>
}
