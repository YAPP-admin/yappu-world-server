package co.yappuworld.user.presentation

import co.yappuworld.global.dto.GlobalResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.presentation.dto.request.UserSignUpRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 인증 API", description = "회원가입, 로그인, 로그아웃 등..")
@Validated
interface UserAuthApi {

    @Operation(summary = "회원가입")
    @PostMapping("/v1/auth/sign-up")
    fun signUp(
        @RequestBody request: UserSignUpRequestDto
    ): ResponseEntity<GlobalResponse<Token>>
}
