package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.application.UserAuthService
import co.yappuworld.user.presentation.dto.request.UserSignUpApiRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class UserAuthController(
    private val userAuthService: UserAuthService
) : UserAuthApi {

    override fun signUp(request: UserSignUpApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val now = LocalDateTime.now()

        if (request.signUpCode.isBlank()) {
            userAuthService.submitSignUpRequest(request.toAppRequest(), now)
            return ResponseEntity.ok(SuccessResponse.of(null))
        }

        return ResponseEntity.ok(
            SuccessResponse.of(userAuthService.signUpWithCode(request.toAppRequest(), now))
        )
    }
}
