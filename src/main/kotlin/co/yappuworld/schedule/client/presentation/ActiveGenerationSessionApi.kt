package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.schedule.client.dto.response.ActiveGenerationSessionsResponseV2
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "활동 기수 세션 API", description = "현재 활성화 된 기수의 세션 API")
interface ActiveGenerationSessionApi {

    @Operation(summary = "현재 활성화 된 기수의 세션 조회")
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
    @GetMapping("/v1/active-generation/sessions")
    fun getActiveGenerationSessions(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<ActiveGenerationSessionsResponseV2>>
}
