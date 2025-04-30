package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "어드민 출석 API", description = "_")
interface AdminAttendanceApi {

    @Operation(summary = "활성화 된 기수의 출석 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AdminAttendancesResponse::class),
                        examples = [
                            ExampleObject(
                                name = "조회",
                                value = """
                                    {
                                        "data": {
                                            "sessions": [
                                                {
                                                    "sessionId": "c07aa77e-1b30-11f0-add0-0242ac140002",
                                                    "name": "가짜 세션1",
                                                    "startDate": "2025-04-18",
                                                    "startDayOfWeek": "금",
                                                    "endDate": "2025-04-18",
                                                    "endDayOfWeek": "금",
                                                    "startTime": "13:30:00",
                                                    "endTime": "17:00:00"
                                                },
                                                {
                                                    "sessionId": "c07afa8b-1b30-11f0-add0-0242ac140002",
                                                    "name": "가짜 세션2",
                                                    "startDate": "2025-05-08",
                                                    "startDayOfWeek": "목",
                                                    "endDate": "2025-05-08",
                                                    "endDayOfWeek": "목",
                                                    "startTime": "13:30:00",
                                                    "endTime": "17:00:00"
                                                }
                                            ],
                                            "users": [
                                                {
                                                    "userId": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                    "name": "홍길동",
                                                    "position": "PM"
                                                },
                                                {
                                                    "userId": "12954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                    "name": "임꺽정",
                                                    "position": "Server"
                                                }
                                            ],
                                            "attendancesGroupedBySession": [
                                                {
                                                    "sessionId": "c07aa77e-1b30-11f0-add0-0242ac140002",
                                                    "attendances": [
                                                        {
                                                            "userId": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                            "status": "결석"
                                                        },
                                                        {
                                                            "userId": "12954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                            "status": "출석"
                                                        }
                                                    ]
                                                },
                                                {
                                                    "sessionId": "c07afa8b-1b30-11f0-add0-0242ac140002",
                                                    "attendances": [
                                                        {
                                                            "userId": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                            "status": null
                                                        },
                                                        {
                                                            "userId": "12954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                            "status": null
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
    @GetMapping("/admin/v1/attendances")
    fun getActiveGenerationAttendances(): ResponseEntity<SuccessResponse<AdminAttendancesResponse>>
}
