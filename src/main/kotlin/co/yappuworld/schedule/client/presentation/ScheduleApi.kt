package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.schedule.client.dto.request.SchedulePageRequest
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponse
import co.yappuworld.schedule.client.dto.response.SchedulePageResponse
import co.yappuworld.schedule.client.dto.response.UpcomingSessionAttendanceResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "스케줄 API", description = "세션 및 기타 일정 조회")
interface ScheduleApi {

    @Operation(summary = "활동 중인 기수의 세션 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "활동 중인 기수의 세션 목록 (길이상 일부 생략)",
                                value = """
                                    {
                                        "data": {
                                            "sessions": [
                                                {
                                                    "id": "55238f62-ff12-11ef-ad31-0242ac120002",
                                                    "name": "OT",
                                                    "place": "강북노동자복지관",
                                                    "date": "2024-11-01",
                                                    "endDate": null,
                                                    "time": "14:00:00",
                                                    "endTime": "18:00:00",
                                                    "type": "OFFLINE",
                                                    "progressPhase": "DONE"
                                                },
                                                {
                                                    "id": "552390a8-ff12-11ef-ad31-0242ac120002",
                                                    "name": "팀세션",
                                                    "place": null,
                                                    "date": "2024-11-01",
                                                    "endDate": null,
                                                    "time": null,
                                                    "endTime": null,
                                                    "type": "TEAM",
                                                    "progressPhase": "DONE"
                                                },
                                                {
                                                    "id": "5523913c-ff12-11ef-ad31-0242ac120002",
                                                    "name": "팀매칭",
                                                    "place": "SBA 산학센터",
                                                    "date": "2024-11-03",
                                                    "endDate": null,
                                                    "time": "14:00:00",
                                                    "endTime": "18:00:00",
                                                    "type": "OFFLINE",
                                                    "progressPhase": "DONE"
                                                }
                                            ],
                                            "upcomingSessionIndex": 2
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
    fun getSessions(): ResponseEntity<SuccessResponse<ActiveGenerationSessionsResponse>>

    @Operation(summary = "일정 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
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
                                                    "schedules": [
                                                        {
                                                            "id": "552390a8-ff12-11ef-ad31-0242ac120002",
                                                            "name": "팀세션",
                                                            "place": null,
                                                            "date": "2024-11-01",
                                                            "endDate": null,
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
                                                            "endDate": null,
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
                                                    "schedules": []
                                                },
                                                {
                                                    "date": "2024-11-03",
                                                    "schedules": [
                                                        {
                                                            "id": "5523913c-ff12-11ef-ad31-0242ac120002",
                                                            "name": "팀매칭",
                                                            "place": "SBA 산학센터",
                                                            "date": "2024-11-03",
                                                            "endDate": null,
                                                            "time": "14:00:00",
                                                            "endTime": "18:00:00",
                                                            "scheduleType": "SESSION",
                                                            "sessionType": "OFFLINE",
                                                            "scheduleProgressPhase": "DONE"
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
        @Valid @ParameterObject request: SchedulePageRequest
    ): ResponseEntity<SuccessResponse<SchedulePageResponse>>

    @Operation(summary = "임박한 세션의 출석 정보")
    @GetMapping("/v1/sessions/upcoming")
    fun getUpcomingSessionAttendance(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UpcomingSessionAttendanceResponse>>
}
