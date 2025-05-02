package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.user.client.application.SignUpService
import co.yappuworld.user.client.application.UserAuthService
import co.yappuworld.user.client.dto.request.CheckingEmailAvailabilityRequest
import co.yappuworld.user.client.dto.request.LatestSignUpApplicationRequest
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.ReissueTokenRequest
import co.yappuworld.user.client.dto.request.UserSignUpRequest
import co.yappuworld.user.client.dto.response.LatestSignUpApplicationResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class UserAuthController(
    private val userAuthService: UserAuthService,
    private val signUpService: SignUpService
) : UserAuthApi {

    override fun signUp(request: UserSignUpRequest): ResponseEntity<SuccessResponse<Token>> {
        val now = getCurrentDateTimeInKST()

        if (request.signUpCode.isNullOrBlank()) {
            signUpService.submitSignUpRequest(request, now)
            return ResponseEntity.created(URI.create("")).build()
        }

        return ResponseEntity.ok(
            SuccessResponse(signUpService.signUpWithCode(request, now))
        )
    }

    override fun login(request: LoginRequest): ResponseEntity<SuccessResponse<Token>> =
        ResponseEntity.ok(
            SuccessResponse(
                userAuthService.login(request, getCurrentDateTimeInKST())
            )
        )

    override fun reissueToken(request: ReissueTokenRequest): ResponseEntity<SuccessResponse<Token>> {
        val token = userAuthService.reissueToken(request, getCurrentDateTimeInKST())
        return ResponseEntity.ok(
            SuccessResponse(token)
        )
    }

    override fun checkEmailAvailability(request: CheckingEmailAvailabilityRequest): ResponseEntity<Unit> {
        signUpService.checkEmailAvailability(request)
        return ResponseEntity.noContent().build()
    }

    override fun findLatestSignUpApplication(
        request: LatestSignUpApplicationRequest
    ): ResponseEntity<SuccessResponse<LatestSignUpApplicationResponse>> =
        ResponseEntity.ok(
            SuccessResponse(userAuthService.findLatestSignUpApplication(request))
        )

    override fun withdrawUser(securityUser: SecurityUser): ResponseEntity<Unit> {
        userAuthService.withdrawUser(securityUser.userId)
        return ResponseEntity.noContent().build()
    }
}
