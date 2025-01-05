package co.yappuworld.user.presentation

import co.yappuworld.global.dto.GlobalResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.application.UserAuthService
import co.yappuworld.user.presentation.dto.request.UserSignUpRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class UserAuthController(
    private val userAuthService: UserAuthService
) : UserAuthApi {

    override fun signUp(request: UserSignUpRequestDto): ResponseEntity<GlobalResponse<Token>> {
        return ResponseEntity.ok(
            GlobalResponse.success(userAuthService.signUp(request, LocalDateTime.now()))
        )
    }
}
