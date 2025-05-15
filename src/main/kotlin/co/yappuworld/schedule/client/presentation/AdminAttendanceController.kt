package co.yappuworld.schedule.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.schedule.client.application.AdminAttendanceService
import co.yappuworld.schedule.client.dto.request.AdminAttendanceCodeUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.request.AdminSessionAttendanceUpdateRequest
import co.yappuworld.schedule.client.dto.response.AdminAttendanceCodeResponse
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

    override fun updateAttendances(request: AdminAttendanceUpdateRequest): ResponseEntity<Unit> {
        adminAttendanceService.updateAttendance(request)
        return ResponseEntity.noContent().build()
    }

    override fun updateSessionAttendances(request: AdminSessionAttendanceUpdateRequest): ResponseEntity<Unit> {
        adminAttendanceService.updateSessionAttendances(request)
        return ResponseEntity.noContent().build()
    }

    override fun getAttendanceCode(): ResponseEntity<SuccessResponse<AdminAttendanceCodeResponse>> =
        ResponseEntity.ok(
            SuccessResponse(adminAttendanceService.getAttendanceCode())
        )

    override fun updateAttendanceCode(request: AdminAttendanceCodeUpdateRequest): ResponseEntity<Unit> {
        adminAttendanceService.updateAttendanceCode(request)
        return ResponseEntity.noContent().build()
    }

    override fun deleteAttendanceCode(): ResponseEntity<Unit> {
        adminAttendanceService.deleteAttendanceCode()
        return ResponseEntity.noContent().build()
    }
}
