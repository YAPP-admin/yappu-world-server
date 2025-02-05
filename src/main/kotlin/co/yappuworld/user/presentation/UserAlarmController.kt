package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.application.UserAlarmService
import co.yappuworld.user.presentation.dto.response.MasterAlarmToggleApiResponse
import co.yappuworld.user.presentation.dto.response.UserAlarmStatusApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAlarmController(
    private val userAlarmService: UserAlarmService
) : UserAlarmApi {

    override fun getAlarmStatus(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UserAlarmStatusApiResponse>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                UserAlarmStatusApiResponse.of(
                    userAlarmService.getAlarmStatus(securityUser.userId)
                )
            )
        )
    }

    override fun toggleMasterAlarm(
        securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<MasterAlarmToggleApiResponse>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                MasterAlarmToggleApiResponse.of(
                    userAlarmService.toggleMasterAlarm(securityUser.userId)
                )
            )
        )
    }
}
