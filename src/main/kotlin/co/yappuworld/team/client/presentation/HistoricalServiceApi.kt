package co.yappuworld.team.client.presentation

import co.yappuworld.global.response.CursorPageResponse
import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.team.client.dto.request.HistoricalServicesPageRequest
import co.yappuworld.team.client.dto.response.HistoricalServiceDetailResponse
import co.yappuworld.team.client.dto.response.HistoricalServicePageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@Tag(name = "역대 서비스 API", description = "역대 서비스 목록 및 상세 조회")
interface HistoricalServiceApi {

    @Operation(summary = "역대 서비스 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "역대 서비스 목록 조회",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "serviceId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab78",
                                                    "generation": 25,
                                                    "serviceName": "역대 서비스",
                                                    "hasApp": true,
                                                    "hasWeb": false,
                                                    "summary": "한 줄 소개입니다.",
                                                    "thumbnailImageUrl": null
                                                }
                                            ],
                                            "lastCursor": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab78",
                                            "limit": 20,
                                            "hasNext": true
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
    @GetMapping("/v1/team-services")
    fun getHistoricalServices(
        @Valid @ParameterObject request: HistoricalServicesPageRequest
    ): ResponseEntity<SuccessResponse<CursorPageResponse<HistoricalServicePageResponse, UUID>>>

    @Operation(summary = "역대 서비스 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = HistoricalServiceDetailResponse::class),
                        examples = [
                            ExampleObject(
                                name = "역대 서비스 상세 조회",
                                value = """
                                    {
                                        "data": {
                                            "serviceId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab78",
                                            "generation": 17,
                                            "serviceName": "무슨무슨 서비스",
                                            "hasApp": false,
                                            "hasWeb": true,
                                            "summary": "서비스의 한 줄 설명입니다.",
                                            "description": "서비스 상세 설명입니다.",
                                            "thumbnailImageUrl": null,
                                            "googlePlayLink": null,
                                            "appStoreLink": null,
                                            "webLink": "https://yapp.co.kr",
                                            "members": [
                                                {
                                                    "activityUnitId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab79",
                                                    "name": "김야푸",
                                                    "position": "PM"
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
            ),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "서비스를 찾을 수 없습니다.",
                                value = """
                                    {
                                        "message": "서비스를 찾을 수 없습니다.",
                                        "errorCode": "TEAM_0002",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/team-services/{serviceId}")
    fun getHistoricalServiceDetail(
        @PathVariable serviceId: UUID
    ): ResponseEntity<SuccessResponse<HistoricalServiceDetailResponse>>
}
