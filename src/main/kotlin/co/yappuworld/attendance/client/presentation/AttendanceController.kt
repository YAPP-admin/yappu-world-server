package co.yappuworld.attendance.client.presentation

import co.yappuworld.attendance.client.application.AttendanceService
import co.yappuworld.attendance.client.dto.request.AttendanceRequest
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.util.TimeUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AttendanceController(
    private val attendanceService: AttendanceService
) : AttendanceApi {

    override fun checkIn(
        request: AttendanceRequest,
        securityUser: SecurityUser
    ): ResponseEntity<Unit> {
        attendanceService.checkIn(request, securityUser.userId, TimeUtils.getCurrentDateTimeInKST())
        return ResponseEntity.created(URI("")).build()
    }
}
