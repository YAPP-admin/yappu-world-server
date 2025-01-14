package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.application.UserAuthService
import co.yappuworld.user.presentation.dto.request.LoginApiRequestDto
import co.yappuworld.user.presentation.dto.request.ReissueTokenApiRequestDto
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

    override fun login(request: LoginApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val now = LocalDateTime.now()
        userAuthService.login(request.toAppRequest(), now)
        TODO("Not yet implemented")
    }

    override fun reissueToken(request: ReissueTokenApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val token = userAuthService.reissueToken(request.toAppRequest(LocalDateTime.now()))
        return ResponseEntity.ok(
            SuccessResponse.of(token)
        )
    }
}
