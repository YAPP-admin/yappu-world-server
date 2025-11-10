package co.yappuworld.team.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.team.client.dto.request.AdminTeamCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamPageRequest
import co.yappuworld.team.client.dto.request.AdminTeamUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamResponse
import co.yappuworld.team.client.dto.response.AdminTeamDetailResponse
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "어드민 팀 API", description = "팀 관리")
interface AdminTeamApi {

    @Operation(summary = "팀 목록")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    schema = Schema(implementation = OffsetPageResponse::class),
                    examples = [
                        ExampleObject(
                            name = "팀 목록 조회",
                            value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "teamId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56gh78",
                                                    "generation": 35,
                                                    "name": "야뿌월드",
                                                    "serviceName": "YAPP World",
                                                    "hasApp": true,
                                                    "hasWeb": false,
                                                    "platformLinks": {
                                                        "app_store": "https://apps.apple.com",
                                                        "google_play": "https://play.google.com/store/apps"
                                                    }
                                                }
                                            ],
                                            "totalCount": 100,
                                            "totalPages": 5,
                                            "page": 1,
                                            "size": 20
                                        },
                                        "isSuccess": true
                                    }
                                """
                        )
                    ]
                )
            ]
        )
    )
    @GetMapping("/admin/v1/teams")
    fun getTeams(
        @ParameterObject request: AdminTeamPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminTeamResponse>>>

    @Operation(summary = "팀 생성")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "잘못된 요청 값",
                                value = """
                                    {
                                        "message": "팀 이름은 필수입니다.",
                                        "errorCode": "TEAM_1004",
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
    @PostMapping("/admin/v1/teams")
    fun createTeam(
        @Valid @RequestBody request: AdminTeamCreateRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "팀 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "팀을 찾을 수 없음",
                                value = """
                                    {
                                        "message": "팀을 찾을 수 없습니다.",
                                        "errorCode": "TEAM_0001",
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
    @PutMapping("/admin/v1/teams/{teamId}")
    fun updateTeam(
        @PathVariable("teamId") teamId: UUID,
        @Valid @RequestBody request: AdminTeamUpdateRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "팀 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "팀을 찾을 수 없음",
                                value = """
                                    {
                                        "message": "팀을 찾을 수 없습니다.",
                                        "errorCode": "TEAM_0001",
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
    @DeleteMapping("/admin/v1/teams/{teamId}")
    fun deleteTeam(
        @PathVariable("teamId") teamId: UUID
    ): ResponseEntity<Unit>

    @Operation(summary = "팀 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AdminTeamDetailResponse::class),
                        examples = [
                            ExampleObject(
                                name = "팀 상세 조회",
                                value = """
                                    {
                                        "data": {
                                            "id": "a1b2c3d4-e5f6-7890-ab12-cd34ef56gh78",
                                            "generation": 35,
                                            "teamName": "야뿌월드",
                                            "serviceName": "YAPP World",
                                            "platforms": ["APP", "WEB"],
                                            "serviceLinks": {
                                                "googleStore": "https://play.google.com/store/apps",
                                                "appStore": "https://apps.apple.com",
                                                "web": "https://yappworld.com"
                                            },
                                            "members": [
                                                {
                                                    "activityUnitId": "activity-uuid",
                                                    "name": "홍길동",
                                                    "position": "PM"
                                                }
                                            ],
                                            "createdAt": "2025-01-01T00:00:00",
                                            "updatedAt": "2025-01-01T00:00:00"
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
                        examples = [
                            ExampleObject(
                                name = "팀을 찾을 수 없음",
                                value = """
                                    {
                                        "message": "팀을 찾을 수 없습니다.",
                                        "errorCode": "TEAM_0001",
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
    @GetMapping("/admin/v1/teams/{teamId}")
    fun getTeam(
        @PathVariable("teamId") teamId: UUID
    ): ResponseEntity<SuccessResponse<AdminTeamDetailResponse>>

}
