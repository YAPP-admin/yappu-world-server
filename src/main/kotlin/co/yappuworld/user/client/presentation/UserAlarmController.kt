package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.application.UserAlarmService
import co.yappuworld.user.client.dto.request.UpdateDeviceAlarmRequest
import co.yappuworld.user.client.dto.response.MasterAlarmToggleResponse
import co.yappuworld.user.client.dto.response.UserAlarmStatusResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAlarmController(
    private val userAlarmService: UserAlarmService
) : UserAlarmApi {

    override fun getAlarmStatus(securityUser: SecurityUser): ResponseEntity<SuccessResponse<UserAlarmStatusResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userAlarmService.getAlarmStatus(securityUser.userId))
        )

    override fun updateDeviceAlarm(
        securityUser: SecurityUser,
        request: UpdateDeviceAlarmRequest
    ): ResponseEntity<Unit> {
        userAlarmService.updateDeviceAlarm(securityUser.userId, request)
        return ResponseEntity.noContent().build()
    }

    override fun toggleMasterAlarm(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<MasterAlarmToggleResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userAlarmService.toggleMasterAlarm(securityUser.userId))
        )
}
