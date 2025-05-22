package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.client.dto.request.AdminSessionCreateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionDeleteRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionPageRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminSessionDetailResponse
import co.yappuworld.schedule.client.dto.response.AdminSessionOverviewResponse
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
interface ScheduleAdminApi {

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
    fun createSchedule(
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
                                name = "목록 제공",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "id": "f70dd406-ffe4-11ef-ad31-0242ac120002",
                                                    "generation": 25,
                                                    "type": "OFFLINE",
                                                    "title": "OT",
                                                    "place": "강북노동자복지관",
                                                    "date": "2024-11-01",
                                                    "time": "14:00:00",
                                                    "endTime": "18:00:00"
                                                },
                                                {
                                                    "id": "f70dd5ff-ffe4-11ef-ad31-0242ac120002",
                                                    "generation": 25,
                                                    "type": "TEAM",
                                                    "title": "팀세션",
                                                    "place": null,
                                                    "date": "2024-11-01",
                                                    "time": null,
                                                    "endTime": null
                                                }
                                            ],
                                            "totalCount": 3,
                                            "totalPages": 2,
                                            "page": 1,
                                            "size": 2
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
    @GetMapping("/admin/v1/sessions")
    fun getSessions(
        @Valid @ParameterObject request: AdminSessionPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSessionOverviewResponse>>>

    @Operation(summary = "세션 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AdminSessionDetailResponse::class),
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "id": "0196ed57-f71d-e55d-8e24-0f4e4f898fae",
                                            "name": "데모데이",
                                            "generation": 25,
                                            "place": "공덕 창업 허브",
                                            "date": "2025-02-27",
                                            "endDate": "2025-02-27",
                                            "time": "14:00:00",
                                            "endTime": "18:00:00",
                                            "sessionType": "OFFLINE",
                                            "participantsByPosition": [
                                                {
                                                    "position": "PM",
                                                    "participants": [
                                                        {
                                                            "id": "0196ed53-5af5-5ef0-2933-109de415f064",
                                                            "name": "홍길동",
                                                            "position": "PM"
                                                        },
                                                        {
                                                            "id": "0196ed53-7aa9-3234-ac58-26b178e94dec",
                                                            "name": "임꺽정",
                                                            "position": "PM"
                                                        }
                                                    ]
                                                },
                                                {
                                                    "position": "Design",
                                                    "participants": []
                                                },
                                                {
                                                    "position": "Web",
                                                    "participants": []
                                                },
                                                {
                                                    "position": "Android",
                                                    "participants": []
                                                },
                                                {
                                                    "position": "iOS",
                                                    "participants": []
                                                },
                                                {
                                                    "position": "Flutter",
                                                    "participants": []
                                                },
                                                {
                                                    "position": "Web",
                                                    "participants": []
                                                },
                                                {
                                                    "position": "Server",
                                                    "participants": []
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
    fun deleteSession(
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
}
