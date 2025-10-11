package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.request.SessionParamRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.SessionDetailsResponse
import co.yappuworld.schedule.client.dto.response.SessionDetailsResponseV2
import co.yappuworld.schedule.client.dto.response.SessionOverviewResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionResponse
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
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@Tag(name = "스케줄 API", description = "일정 및 출석")
interface ScheduleApi {

    @Operation(summary = "전체 일정 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "일정 목록 (길이상 일부 생략)",
                                value = """
                                    {
                                        "data": {
                                            "dates": [
                                                {
                                                    "date": "2024-11-01",
                                                    "isToday": false,
                                                    "dayOfTheWeek": "금",
                                                    "schedules": [
                                                        {
                                                            "id": "552390a8-ff12-11ef-ad31-0242ac120002",
                                                            "name": "팀세션",
                                                            "place": null,
                                                            "date": "2024-11-01",
                                                            "startDayOfTheWeek": "금",
                                                            "endDate": null,
                                                            "endDayOfTheWeek": "금",
                                                            "time": null,
                                                            "endTime": null,
                                                            "scheduleType": "SESSION",
                                                            "sessionType": "TEAM",
                                                            "scheduleProgressPhase": "DONE"
                                                        },
                                                        {
                                                            "id": "55238f62-ff12-11ef-ad31-0242ac120002",
                                                            "name": "OT",
                                                            "place": "강북노동자복지관",
                                                            "date": "2024-11-01",
                                                            "startDayOfTheWeek": "금",
                                                            "endDate": null,
                                                            "endDayOfTheWeek": "금",
                                                            "time": "14:00:00",
                                                            "endTime": "18:00:00",
                                                            "scheduleType": "SESSION",
                                                            "sessionType": "OFFLINE",
                                                            "scheduleProgressPhase": "DONE"
                                                        }
                                                    ]
                                                },
                                                {
                                                    "date": "2024-11-02",
                                                    "dayOfTheWeek": "토",
                                                    "isToday": true,
                                                    "schedules": []
                                                },
                                                {
                                                    "date": "2024-11-03",
                                                    "isToday": false,
                                                    "dayOfTheWeek": "일",
                                                    "schedules": [
                                                        {
                                                            "id": "5523913c-ff12-11ef-ad31-0242ac120002",
                                                            "name": "팀매칭",
                                                            "place": "SBA 산학센터",
                                                            "date": "2024-11-03",
                                                            "startDayOfTheWeek": "일",
                                                            "endDate": null,
                                                            "endDayOfTheWeek": "일",
                                                            "time": "14:00:00",
                                                            "endTime": "18:00:00",
                                                            "scheduleType": "SESSION",
                                                            "sessionType": "OFFLINE",
                                                            "scheduleProgressPhase": "DONE",
                                                            "attendanceStatus": "결석"
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
    @GetMapping("/v1/schedules")
    fun getSchedules(
        @Valid @ParameterObject request: SchedulePageRequest,
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<SchedulePageResponse>>

    @Operation(summary = "활동 중인 기수의 세션 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "활동 중인 기수의 세션 목록 (25년 4월 29일 기준)",
                                value = """
                                    {
                                        "data": {
                                            "sessions": [
                                                {
                                                    "id": "c07aa77e-1b30-11f0-add0-0242ac140002",
                                                    "name": "가짜 세션1",
                                                    "place": "아몰랑",
                                                    "date": "2025-04-18",
                                                    "startDayOfWeek": "금",
                                                    "endDate": "2025-04-18",
                                                    "endDayOfWeek": "금",
                                                    "relativeDays": 11,
                                                    "time": "13:30:00",
                                                    "endTime": "17:00:00",
                                                    "type": "OFFLINE",
                                                    "progressPhase": "DONE",
                                                    "attendanceStatus": "결석"
                                                },
                                                {
                                                    "id": "c07afa8b-1b30-11f0-add0-0242ac140002",
                                                    "name": "가짜 세션2",
                                                    "place": "아몰랑",
                                                    "date": "2025-05-08",
                                                    "startDayOfWeek": "목",
                                                    "endDate": "2025-05-08",
                                                    "endDayOfWeek": "목",
                                                    "relativeDays": -9,
                                                    "time": "13:30:00",
                                                    "endTime": "17:00:00",
                                                    "type": "OFFLINE",
                                                    "progressPhase": "UPCOMING",
                                                    "attendanceStatus": null
                                                },
                                                {
                                                    "id": "c07afa8b-1b30-11f0-add0-0242ac140003",
                                                    "name": "가짜 세션3",
                                                    "place": "아몰랑",
                                                    "date": "2025-05-11",
                                                    "startDayOfWeek": "일",
                                                    "endDate": "2025-05-11",
                                                    "endDayOfWeek": "일",
                                                    "relativeDays": -12,
                                                    "time": "13:30:00",
                                                    "endTime": "17:00:00",
                                                    "type": "OFFLINE",
                                                    "progressPhase": "PENDING",
                                                    "attendanceStatus": null
                                                }
                                            ],
                                            "upcomingSessionId": "c07afa8b-1b30-11f0-add0-0242ac140002"
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
    @GetMapping("/v1/sessions")
    fun getSessions(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<ActiveGenerationSessionsResponse>>

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
                                name = "세션 목록 조회",
                                value = """
                                    {
                                        "data": {
                                            "sessions": [
                                                {
                                                    "id": "c07aa77e-1b30-11f0-add0-0242ac140002",
                                                    "name": "가짜 세션1",
                                                    "place": "아몰랑",
                                                    "date": "2025-04-18",
                                                    "startDayOfWeek": "금",
                                                    "endDate": "2025-04-18",
                                                    "endDayOfWeek": "금",
                                                    "relativeDays": 11,
                                                    "time": "13:30:00",
                                                    "endTime": "17:00:00",
                                                    "type": "OFFLINE",
                                                    "progressPhase": "종료",
                                                    "attendanceStatus": "결석"
                                                }
                                            ],
                                            "upcomingSessionId": "c07afa8b-1b30-11f0-add0-0242ac140002"
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
    @GetMapping("/v2/sessions")
    fun getSessions(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @Valid @ParameterObject request: SessionParamRequest
    ): ResponseEntity<SuccessResponse<SessionOverviewResponse>>

    @Operation(summary = "임박한 세션의 출석 정보")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = UpcomingSessionResponse::class),
                        examples = [
                            ExampleObject(
                                name = "활동 유저가 아니거나 미출석 & 출석 가능한 시간이 아닌 경우",
                                value = """
                                    {
                                        "data": {
                                            "sessionId": "552390a8-ff12-11ef-ad31-0242ac120002",
                                            "name": "OT",
                                            "startDate": "2025-05-08",
                                            "startDayOfWeek": "목",
                                            "endDate": "2025-05-08",
                                            "endDayOfWeek": "목",
                                            "startTime": "13:30:00",
                                            "endTime": "17:00:00",
                                            "place": "아몰랑",
                                            "relativeDays": -1,
                                            "canCheckIn": false,
                                            "status": null,
                                            "notices": [
                                                {
                                                    "id": "5523a1f4-ff12-11ef-ad31-0242ac120002",
                                                    "title": "[공지] OT 안내사항"
                                                }
                                            ]
                                        },
                                        "isSuccess": true
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "미출석 & 출석 가능한 시간",
                                value = """
                                    {
                                        "data": {
                                            "sessionId": "552390a8-ff12-11ef-ad31-0242ac120002",
                                            "name": "OT",
                                            "startDate": "2025-05-08",
                                            "startDayOfWeek": "목",
                                            "endDate": "2025-05-08",
                                            "endDayOfWeek": "목",
                                            "startTime": "13:30:00",
                                            "endTime": "17:00:00",
                                            "place": "아몰랑",
                                            "relativeDays": 0,
                                            "canCheckIn": true,
                                            "status": null,
                                            "notices": [
                                                {
                                                    "id": "5523a1f4-ff12-11ef-ad31-0242ac120002",
                                                    "title": "[공지] OT 안내사항"
                                                }
                                            ]
                                        },
                                        "isSuccess": true
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "출석한 상태",
                                value = """
                                    {
                                        "data": {
                                            "sessionId": "552390a8-ff12-11ef-ad31-0242ac120002",
                                            "name": "OT",
                                            "startDate": "2025-05-08",
                                            "startDayOfWeek": "목",
                                            "endDate": "2025-05-08",
                                            "endDayOfWeek": "목",
                                            "startTime": "13:30:00",
                                            "endTime": "17:00:00",
                                            "place": "아몰랑",
                                            "relativeDays": 0,
                                            "canCheckIn": false,
                                            "status": "출석",
                                            "notices": [
                                                {
                                                    "id": "5523a1f4-ff12-11ef-ad31-0242ac120002",
                                                    "title": "[공지] OT 안내사항"
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
                                name = "다음 세션이 없는 경우",
                                value = """
                                    {
                                        "message": "예정된 세션이 존재하지 않습니다.",
                                        "errorCode": "SCH_1005",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "활성화 된 기수가 없는 경우",
                                value = """
                                    {
                                        "message": "활성화 된 기수가 없어서 임박한 세션이 존재하지 않습니다.",
                                        "errorCode": "SCH_1006",
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
    @GetMapping("/v1/sessions/upcoming")
    fun getUpcomingSessionAttendance(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UpcomingSessionResponse>>

    @Operation(summary = "세션 상세 조회")
    @ApiResponses()
    @GetMapping("/v1/sessions/{sessionId}")
    fun getSessionDetails(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @PathVariable sessionId: UUID
    ): ResponseEntity<SuccessResponse<SessionDetailsResponse>>

    @Operation(summary = "세션 상세 조회 (ISO 8601)")
    @ApiResponses()
    @GetMapping("/v2/sessions/{sessionId}")
    fun getSessionDetailsV2(
        @AuthenticationPrincipal securityUser: SecurityUser,
        @PathVariable sessionId: UUID
    ): ResponseEntity<SuccessResponse<SessionDetailsResponseV2>>
}
