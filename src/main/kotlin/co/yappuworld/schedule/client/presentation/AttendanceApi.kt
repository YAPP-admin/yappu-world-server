package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.client.dto.response.AttendanceStatisticsResponse
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "스케줄 API", description = "일정 및 출석")
interface AttendanceApi {

    @Operation(summary = "출석")
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
                                name = "출석코드 오류",
                                value = """
                                    {
                                        "errorCode": "ATD_1001",
                                        "message": "출석 코드가 일치하지 않습니다.",
                                        "isSuccess": false
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "세션 아닌 일정에 출석",
                                value = """
                                    {
                                        "errorCode": "ATD_2001",
                                        "message": "출석은 세션 타입 일정에만 할 수 있습니다.",
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
                                name = "출석 대상 세션 오류",
                                value = """
                                    {
                                        "errorCode": "ATD_2000",
                                        "message": "출석 대상 세션을 찾을 수 없습니다.",
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
                                name = "이미 출석을 완료",
                                value = """
                                    {
                                        "errorCode": "ATD_1000",
                                        "message": "이미 출석 체크를 하였습니다.",
                                        "isSuccess": false
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "활성화 된 기수가 없어 출석 불가",
                                value = """
                                    {
                                        "errorCode": "ATD_2002",
                                        "message": "활성화 된 기수가 없어서 출석 관련 처리가 불가합니다.",
                                        "isSuccess": false
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "해당 기수에 활동 기록이 없음",
                                value = """
                                    {
                                        "errorCode": "ATD_2007",
                                        "message": "해당 기수에 활동이 없어서 출석 관련 처리가 불가합니다.",
                                        "isSuccess": false
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "해당 기수에 운영진이 아닌 활동 기록이 없음",
                                value = """
                                    {
                                        "errorCode": "ATD_2002",
                                        "message": "해당 기수에 참가자 직군으로 활동이 없어서 출석 관련 처리가 불가합니다.",
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
                                name = "출석 코드 설정 에러",
                                value = """
                                    {
                                        "errorCode": "ATD_0001",
                                        "message": "출석 코드를 찾을 수 없습니다.",
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
    @PostMapping("/v1/attendances")
    fun checkIn(
        @Valid @RequestBody request: AttendanceRequest,
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<Unit>

    @Operation(summary = "나의 출석 통계")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AttendanceStatisticsResponse::class),
                        examples = [
                            ExampleObject(
                                name = "출석 통계 조회",
                                value = """
                                    {
                                        "data": {
                                            "totalSessionCount": 17,
                                            "remainingSessionCount": 2,
                                            "sessionProgressRate": 88.2,
                                            "attendancePoint": 40,
                                            "attendanceCount": 10,
                                            "lateCount": 3,
                                            "absenceCount": 2,
                                            "latePassCount": 1
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
                                name = "활성화 된 기수가 없어 출석 통계 조회 불가",
                                value = """
                                    {
                                        "errorCode": "ATD_2002",
                                        "message": "활성화 된 기수가 없어서 출석 통계 조회가 불가합니다.",
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
    @GetMapping("/v1/attendances/statistics")
    fun getAttendanceStatistics(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendanceStatisticsResponse>>

    @Operation(summary = "출석 내역 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AttendancesHistoryResponse::class),
                        examples = [
                            ExampleObject(
                                name = "출석 내역 조회",
                                value = """
                                    {
                                        "data": {
                                            "histories": [
                                                {
                                                    "sessionId": "c076cadd-1b30-11f0-add0-0242ac140002",
                                                    "name": "OT",
                                                    "checkedInAt": "2025-04-17T13:18:17",
                                                    "attendanceStatus": "출석"
                                                },
                                                {
                                                    "sessionId": "c076ff63-1b30-11f0-add0-0242ac140002",
                                                    "name": "팀 매칭",
                                                    "checkedInAt": "2025-04-17T13:18:17",
                                                    "attendanceStatus": "지각"
                                                },
                                                {
                                                    "sessionId": "c0775226-1b30-11f0-add0-0242ac140002",
                                                    "name": "팀 세션",
                                                    "checkedInAt": "2025-04-17T13:18:17",
                                                    "attendanceStatus": "결석"
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
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "활성화 된 기수 없음",
                                value = """
                                    {
                                        "errorCode": "ATD_2002",
                                        "message": "활성화 된 기수가 없어서 출석 관련 처리가 불가합니다.",
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
    @GetMapping("/v1/attendances/history")
    fun getAttendancesHistory(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendancesHistoryResponse>>
}
