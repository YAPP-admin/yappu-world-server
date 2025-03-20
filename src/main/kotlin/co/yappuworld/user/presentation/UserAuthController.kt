package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.TimeUtils.getCurrentDateTimeInKST
import co.yappuworld.user.application.SignUpService
import co.yappuworld.user.application.UserAuthService
import co.yappuworld.user.application.dto.request.LoginRequest
import co.yappuworld.user.presentation.dto.request.CheckingEmailAvailabilityApiRequestDto
import co.yappuworld.user.presentation.dto.request.LatestSignUpApplicationApiRequestDto
import co.yappuworld.user.presentation.dto.request.ReissueTokenApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserSignUpApiRequestDto
import co.yappuworld.user.presentation.dto.response.LatestSignUpApplicationApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UserAuthController(
    private val userAuthService: UserAuthService,
    private val signUpService: SignUpService
) : UserAuthApi {

    override fun signUp(request: UserSignUpApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val now = getCurrentDateTimeInKST()

        if (request.signUpCode.isNullOrBlank()) {
            signUpService.submitSignUpRequest(request.toAppRequest(), now)
            return ResponseEntity.created(URI.create("")).build()
        }

        return ResponseEntity.ok(
            SuccessResponse(signUpService.signUpWithCode(request.toAppRequest(), now))
        )
    }

    override fun login(request: LoginRequest): ResponseEntity<SuccessResponse<Token>> {
        val now = getCurrentDateTimeInKST()
        return ResponseEntity.ok(
            SuccessResponse(
                userAuthService.login(request, now)
            )
        )
    }

    override fun reissueToken(request: ReissueTokenApiRequestDto): ResponseEntity<SuccessResponse<Token>> {
        val token = userAuthService.reissueToken(request.toAppRequest(getCurrentDateTimeInKST()))
        return ResponseEntity.ok(
            SuccessResponse(token)
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
    ): ResponseEntity<SuccessResponse<LatestSignUpApplicationApiResponseDto>> =
        ResponseEntity.ok(
            SuccessResponse(
                LatestSignUpApplicationApiResponseDto.of(
                    signUpService.findLatestSignUpApplication(request.toAppRequest())
                )
            )
        )

    override fun withdrawUser(securityUser: SecurityUser): ResponseEntity<Unit> {
        userAuthService.withdrawUser(securityUser.userId)
        return ResponseEntity.noContent().build()
    }
}
