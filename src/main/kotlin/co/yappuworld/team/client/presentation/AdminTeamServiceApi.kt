package co.yappuworld.team.client.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.team.client.dto.request.AdminTeamServiceCreateRequest
import co.yappuworld.team.client.dto.request.AdminTeamServicePageRequest
import co.yappuworld.team.client.dto.request.AdminTeamServiceUpdateRequest
import co.yappuworld.team.client.dto.response.AdminTeamServiceDetailResponse
import co.yappuworld.team.client.dto.response.AdminTeamServiceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Tag(name = "어드민 팀 서비스 API", description = "팀 서비스 관리")
interface AdminTeamServiceApi {
    @Operation(summary = "팀 서비스 목록")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            content = [
                Content(
                    examples = [
                        ExampleObject(
                            name = "팀 서비스 목록 조회",
                            value = """
                                {
                                    "data": {
                                        "data": [
                                            {
                                                "serviceId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab78",
                                                "generation": 35,
                                                "teamName": "야뿌월드",
                                                "serviceName": "YAPP World",
                                                "hasApp": true,
                                                "hasWeb": false,
                                                "summary": "서비스 소개입니다.",
                                                "isOperating": true
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
    @GetMapping("/admin/v1/team-services")
    fun getTeamServices(
        @ParameterObject request: AdminTeamServicePageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminTeamServiceResponse>>>

    @Operation(summary = "팀 서비스 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "팀 서비스 상세 조회",
                                value = """
                                {
                                    "data": {
                                        "serviceId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab78",
                                        "generation": 35,
                                        "teamName": "야뿌월드",
                                        "serviceName": "YAPP World",
                                        "hasApp": true,
                                        "hasWeb": false,
                                        "googlePlayLink": "https://play.google.com/store/apps",
                                        "appStoreLink": "https://apps.apple.com",
                                        "webLink": null,
                                        "thumbnailImageUrl": "https://image.yapp.co.kr/service-thumbnail.png",
                                        "isOperating": true,
                                        "summary": "서비스 소개입니다.",
                                        "description": "상세 설명입니다."
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
    @GetMapping("/admin/v1/team-services/{serviceId}")
    fun getTeamService(
        @PathVariable("serviceId") serviceId: UUID
    ): ResponseEntity<SuccessResponse<AdminTeamServiceDetailResponse>>

    @Operation(summary = "팀 서비스 생성")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", content = [Content()]),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "소개글 길이 초과",
                                value = """
                                {
                                    "message": "소개 글은 500자 이하여야 합니다.",
                                    "errorCode": "COM_0002",
                                    "errors": [
                                        {
                                            "field": "summary",
                                            "message": "소개 글은 500자 이하여야 합니다."
                                        }
                                    ],
                                    "isSuccess": false
                                }
                            """
                            ),
                            ExampleObject(
                                name = "유효하지 않은 URL",
                                value = """
                                {
                                    "message": "유효한 URL 형식이어야 합니다",
                                    "errorCode": "COM_0002",
                                    "errors": [
                                        {
                                            "field": "googlePlayLink",
                                            "message": "유효한 URL 형식이어야 합니다"
                                        }
                                    ],
                                    "isSuccess": false
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
                                name = "팀을 찾을 수 없습니다.",
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
    @PostMapping(
        "/admin/v1/team-services",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun createTeamService(
        @Valid @RequestPart("request") request: AdminTeamServiceCreateRequest,
        @RequestPart("thumbnailImage", required = false) thumbnailImage: MultipartFile?
    ): ResponseEntity<Unit>

    @Operation(summary = "팀 서비스 수정")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", content = [Content()]),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "소개글 길이 초과",
                                value = """
                                {
                                    "message": "소개 글은 500자 이하여야 합니다.",
                                    "errorCode": "COM_0002",
                                    "errors": [
                                        {
                                            "field": "summary",
                                            "message": "소개 글은 500자 이하여야 합니다."
                                        }
                                    ],
                                    "isSuccess": false
                                }
                            """
                            ),
                            ExampleObject(
                                name = "유효하지 않은 URL",
                                value = """
                                {
                                    "message": "유효한 URL 형식이어야 합니다",
                                    "errorCode": "COM_0002",
                                    "errors": [
                                        {
                                            "field": "googlePlayLink",
                                            "message": "유효한 URL 형식이어야 합니다"
                                        }
                                    ],
                                    "isSuccess": false
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
    @PutMapping(
        "/admin/v1/team-services",
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    fun updateTeamService(
        @Valid @RequestPart("request") request: AdminTeamServiceUpdateRequest,
        @RequestPart("thumbnailImage", required = false) thumbnailImage: MultipartFile?
    ): ResponseEntity<Unit>

    @Operation(summary = "팀 서비스 삭제")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "204", content = [Content()]),
            ApiResponse(
                responseCode = "404",
                content = [
                    Content(
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
    @DeleteMapping("/admin/v1/team-services/{serviceId}")
    fun deleteTeamService(
        @PathVariable("serviceId") serviceId: UUID
    ): ResponseEntity<Unit>
}
