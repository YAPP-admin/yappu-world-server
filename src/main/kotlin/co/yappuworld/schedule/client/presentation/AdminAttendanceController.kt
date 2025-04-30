package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.util.TimeUtils.getCurrentDateTimeInKST
import co.yappuworld.schedule.client.application.AdminAttendanceService
import co.yappuworld.schedule.client.dto.response.AdminAttendancesResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminAttendanceController(
    private val adminAttendanceService: AdminAttendanceService
) : AdminAttendanceApi {

    override fun getActiveGenerationAttendances(): ResponseEntity<SuccessResponse<AdminAttendancesResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminAttendanceService.findAttendances(getCurrentDateTimeInKST()))
        )
}
