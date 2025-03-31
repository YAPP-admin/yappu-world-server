package co.yappuworld.user.client.presentation

import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.application.UserAlarmService
import co.yappuworld.user.client.dto.request.UpdateFcmTokenRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserSystemController(
    private val userAlarmService: UserAlarmService
) : UserSystemApi {

    override fun updateFcmToken(
        securityUser: SecurityUser,
        request: UpdateFcmTokenRequest
    ): ResponseEntity<Unit> {
        userAlarmService.updateFcmToken(securityUser.userId, request.fcmToken)
        return ResponseEntity.noContent().build()
    }
}
