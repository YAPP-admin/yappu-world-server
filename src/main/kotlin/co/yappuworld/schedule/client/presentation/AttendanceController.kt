package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.util.DatetimeUtils
import co.yappuworld.schedule.client.application.AttendanceService
import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.client.dto.response.AttendanceStatisticsResponse
import co.yappuworld.schedule.client.dto.response.AttendanceStatisticsResponseV2
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponse
import co.yappuworld.schedule.client.dto.response.AttendancesHistoryResponseV2
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.time.LocalDateTime

@RestController
class AttendanceController(
    private val attendanceService: AttendanceService
) : AttendanceApi {

    override fun checkIn(
        request: AttendanceRequest,
        securityUser: SecurityUser
    ): ResponseEntity<Unit> {
        attendanceService.checkIn(request, securityUser.userId, DatetimeUtils.getCurrentDateTimeInKST())
        return ResponseEntity.created(URI("")).build()
    }

    override fun getAttendanceStatistics(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendanceStatisticsResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                AttendanceStatisticsResponse.from(
                    attendanceService.getAttendanceStatistics(securityUser.userId, LocalDateTime.now())
                )
            )
        )

    override fun getAttendanceStatisticsV2(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendanceStatisticsResponseV2>> =
        ResponseEntity.ok(
            SuccessResponse(
                AttendanceStatisticsResponseV2.from(
                    attendanceService.getAttendanceStatistics(securityUser.userId, LocalDateTime.now())
                )
            )
        )

    override fun getAttendancesHistory(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendancesHistoryResponse>> =
        ResponseEntity.ok(
            SuccessResponse(
                attendanceService.getAttendancesHistory(securityUser.userId, DatetimeUtils.getCurrentDateTimeInKST())
            )
        )

    override fun getAttendancesHistoryV2(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AttendancesHistoryResponseV2>> =
        ResponseEntity.ok(
            SuccessResponse(
                attendanceService.getAttendancesHistoryV2(
                    securityUser.userId,
                    DatetimeUtils.getCurrentDateTimeInKST()
                )
            )
        )
}
