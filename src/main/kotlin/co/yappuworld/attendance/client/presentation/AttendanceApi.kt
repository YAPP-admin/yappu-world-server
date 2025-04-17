package co.yappuworld.attendance.client.presentation

import co.yappuworld.attendance.client.dto.request.AttendanceRequest
import co.yappuworld.attendance.client.dto.response.AttendanceStatisticsResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "출석 API", description = "_")
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
                                name = "기수 정보 오류",
                                value = """
                                    {
                                        "errorCode": "ATD_2003",
                                        "message": "출석 처리를 위한 기수 정보가 올바르지 않습니다.",
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
    @GetMapping("/v1/attendance-statistics")
    fun getAttendanceStatistics(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendanceStatisticsResponse>>
}
