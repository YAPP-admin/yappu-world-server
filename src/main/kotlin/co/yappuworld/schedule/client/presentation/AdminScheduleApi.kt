package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionEligibleUsersParamRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSimpleSessionNoticePageRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionEligibleUsersResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
import co.yappuworld.schedule.client.dto.response.AdminTargetableSessionNoticeResponse
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "어드민 일정 API", description = "일정 및 출석 관리")
interface AdminScheduleApi {

    @Operation(summary = "세션 생성")
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
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "세션 시작 일시가 종료 일시보다 늦을 수 없음",
                                value = """
                                    {
                                        "message": "시작 시간이 종료 시간보다 늦을 수 없습니다.",
                                        "errorCode": "SCH_4000",
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
    @PostMapping("/admin/v1/sessions")
    fun createSession(
        @Valid @RequestBody request: AdminSessionCreateRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "세션 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "제목 검색 및 필터 적용 예시",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "id": "f70dd406-ffe4-11ef-ad31-0242ac120002",
                                                    "generation": 25,
                                                    "type": "OFFLINE",
                                                    "title": "25기 오프라인 세션",
                                                    "place": "강북노동자복지관",
                                                    "date": "2025-02-15",
                                                    "endDate": "2025-02-15",
                                                    "time": "14:00:00",
                                                    "endTime": "18:00:00"
                                                }
                                            ],
                                            "totalCount": 1,
                                            "totalPages": 1,
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
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "잘못된 페이지 파라미터",
                                value = """
                                    {
                                        "message": "페이지 번호는 1 이상이어야 합니다.",
                                        "errorCode": "GLB_0002",
                                        "errors": [
                                            {
                                                "field": "page",
                                                "message": "페이지 번호는 1 이상이어야 합니다."
                                            }
                                        ],
                                        "isSuccess": false
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "잘못된 페이지 크기 파라미터",
                                value = """
                                    {
                                        "message": "페이지 당 데이터 개수는 1 이상이어야 합니다.",
                                        "errorCode": "GLB_0002",
                                        "errors": [
                                            {
                                                "field": "size",
                                                "message": "페이지 당 데이터 개수는 1 이상이어야 합니다."
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
                responseCode = "500",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "서버 오류",
                                value = """
                                    {
                                        "message": "서버 에러가 발생했습니다.",
                                        "errorCode": "GLB_0001",
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
    @GetMapping("/admin/v1/sessions")
    fun getSessions(
        @Valid @ParameterObject request: AdminSessionPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewResponse>>>

    @Operation(summary = "세션 상세 조회")
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
                                            "id": "f70dd5ff-ffe4-11ef-ad31-0242ac120002",
                                            "name": "팀세션",
                                            "generation": 25,
                                            "place": null,
                                            "date": "2024-11-01",
                                            "time": "17:00:00",
                                            "endTime": "20:00:00",
                                            "sessionType": "TEAM",
                                            "attendees": [
                                                 {
                                                     "position": "PM",
                                                     "attendees": [
                                                        {
                                                            "userId": "f70dd5ff-ffe4-11ef-ad31-0242ac120002",
                                                            "name": "홍길동",
                                                            "position": "PM"
                                                        }
                                                     ]
                                                 },
                                                 {
                                                     "position": "DESIGN",
                                                     "attendees": []
                                                 },
                                                 {
                                                     "position": "WEB",
                                                     "attendees": []
                                                 },
                                                 {
                                                     "position": "ANDROID",
                                                     "attendees": []
                                                 },
                                                 {
                                                     "position": "IOS",
                                                     "attendees": []
                                                 },
                                                 {
                                                     "position": "FLUTTER",
                                                     "attendees": []
                                                 },
                                                 {
                                                     "position": "SERVER",
                                                     "attendees": []
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
                                name = "ID와 일치하는 세션이 존재하지 않습니다.",
                                value = """
                                    {
                                        "message": "세션을 찾지 못했습니다.",
                                        "errorCode": "SCH_1002",
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
    @GetMapping("/admin/v1/sessions/{sessionId}")
    fun getSession(
        @PathVariable sessionId: UUID
    ): ResponseEntity<SuccessResponse<AdminSessionDetailResponse>>

    @Operation(summary = "세션 삭제")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "ID와 일치하는 세션이 존재하지 않습니다.",
                                value = """
                                    {
                                        "message": "삭제할 수 없는 ID가 포함되어 있습니다.",
                                        "errorCode": "SCH_1003",
                                        "isSuccess": false
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "요청 ID 리스트가 비어있는 경우",
                                value = """
                                    {
                                        "message": "삭제할 ID는 하나 이상이어야 합니다.",
                                        "errorCode": "COM_0002",
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
    @DeleteMapping("/admin/v1/sessions")
    fun deleteSessions(
        @Valid @RequestBody request: AdminSessionDeleteRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "세션 수정")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "세션 타입이 아닌 일정에 대한 수정 요청",
                                value = """
                                    {
                                        "message": "세션 타입 수정만 요청 가능합니다.",
                                        "errorCode": "SCH_1003",
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
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "ID와 일치하는 세션이 존재하지 않습니다.",
                                value = """
                                    {
                                        "message": "세션을 찾지 못했습니다.",
                                        "errorCode": "SCH_1002",
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
    @PutMapping("/admin/v1/sessions")
    fun updateSession(
        @RequestBody request: AdminSessionUpdateRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "세션 참석 가능한 유저 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AdminSessionEligibleUsersResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "users": [
                                                {
                                                    "position": "PM",
                                                    "users": [
                                                        {
                                                            "userId": "01971fa9-f620-7579-a0bf-eb79e3ffd31a",
                                                            "name": "홍길동"
                                                        },
                                                        {
                                                            "userId": "01971faa-2675-9a41-e1e1-ecf1f81504ca",
                                                            "name": "임꺽정"
                                                        }
                                                    ]
                                                },
                                                {
                                                    "position": "DESIGN",
                                                    "users": []
                                                },
                                                {
                                                    "position": "WEB",
                                                    "users": [
                                                        {
                                                            "userId": "01971faa-6929-0843-8f00-91c31b7c6650",
                                                            "name": "진달래"
                                                        }
                                                    ]
                                                },
                                                {
                                                    "position": "ANDROID",
                                                    "users": []
                                                },
                                                {
                                                    "position": "IOS",
                                                    "users": []
                                                },
                                                {
                                                    "position": "FLUTTER",
                                                    "users": []
                                                },
                                                {
                                                    "position": "SERVER",
                                                    "users": [
                                                        {
                                                            "userId": "01971faa-a1c3-7a64-2dd9-0aad37df97c7",
                                                            "name": "장미"
                                                        }
                                                    ]
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
    @GetMapping("/admin/v1/session-eligible-users")
    fun getSessionEligibleUsers(
        @Valid @ParameterObject request: AdminSessionEligibleUsersParamRequest
    ): ResponseEntity<SuccessResponse<AdminSessionEligibleUsersResponse>>

    @Operation(summary = "세션 공지사항으로 선택할 수 있는 목록 조회")
    @GetMapping("/admin/v1/sessions/targetable-notices")
    fun getTargetNotices(
        @Valid @ParameterObject request: AdminSimpleSessionNoticePageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminTargetableSessionNoticeResponse>>>
}
