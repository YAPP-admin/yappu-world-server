package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.application.UserAuthService
import co.yappuworld.user.application.SignUpService
import co.yappuworld.user.presentation.dto.request.CheckingEmailAvailabilityApiRequestDto
import co.yappuworld.user.presentation.dto.request.LatestSignUpApplicationApiRequestDto
import co.yappuworld.user.presentation.dto.request.LoginApiRequestDto
import co.yappuworld.user.presentation.dto.request.ReissueTokenApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserSignUpApiRequestDto
import co.yappuworld.user.presentation.dto.response.LatestSignUpApplicationApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
class UserAuthController(
    private val userAuthService: UserAuthService,
    private val signUpService: SignUpService
) : UserAuthApi {

    override fun signUp(request: UserSignUpApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val now = LocalDateTime.now()

        if (request.signUpCode.isBlank()) {
            signUpService.submitSignUpRequest(request.toAppRequest(), now)
            return ResponseEntity.ok(SuccessResponse.of(null))
        }

        return ResponseEntity.ok(
            SuccessResponse.of(signUpService.signUpWithCode(request.toAppRequest(), now))
        )
    }

    override fun login(request: LoginApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val now = LocalDateTime.now()
        return ResponseEntity.ok(
            SuccessResponse.of(
                userAuthService.login(request.toAppRequest(), now)
            )
        )
    }

    override fun reissueToken(request: ReissueTokenApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val token = userAuthService.reissueToken(request.toAppRequest(LocalDateTime.now()))
        return ResponseEntity.ok(
            SuccessResponse.of(token)
        )
    }

    override fun checkEmailAvailability(
        request: CheckingEmailAvailabilityApiRequestDto
    ): ResponseEntity<SuccessResponse<Unit>> {
        signUpService.checkEmailAvailability(request.toAppRequest())
        return ResponseEntity.ok(SuccessResponse())
    }

    override fun findLatestSignUpApplication(
        request: LatestSignUpApplicationApiRequestDto
    ): ResponseEntity<SuccessResponse<LatestSignUpApplicationApiResponseDto>> {
        return ResponseEntity.ok(
            SuccessResponse.of(
                LatestSignUpApplicationApiResponseDto.of(
                    signUpService.findLatestSignUpApplication(request.toAppRequest())
                )
            )
        )
    }

    override fun withdrawUser(securityUser: SecurityUser): ResponseEntity<Unit> {
        userAuthService.withdrawUser(securityUser.userId)
        return ResponseEntity.noContent().build()
    }
}
