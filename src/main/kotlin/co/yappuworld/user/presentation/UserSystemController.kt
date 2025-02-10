package co.yappuworld.user.presentation

import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.application.UserAlarmService
import co.yappuworld.user.presentation.dto.request.UpdateFcmTokenApiRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserSystemController(
    private val userAlarmService: UserAlarmService
) : UserSystemApi {

    override fun updateFcmToken(
        securityUser: SecurityUser,
        request: UpdateFcmTokenApiRequestDto
    ): ResponseEntity<Unit> {
        userAlarmService.updateFcmToken(securityUser.userId, request.fcmToken)
        return ResponseEntity.noContent().build()
    }
}
