package co.yappuworld.user.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.presentation.dto.request.UserSignUpApiRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 인증 API", description = "회원가입, 로그인, 로그아웃 등..")
@Validated
interface UserAuthApi {

    @Operation(summary = "회원가입")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "인증에 성공하여 회원가입까지 완료",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "accessToken": "accessToken...",
                                            "refreshToken": "refreshToken..."
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "인증 번호 오류",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "errorCode": "USR-0001",
                                        "message": "매칭되는 인증번호가 존재하지 않습니다."
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/v1/auth/sign-up")
    fun signUp(
        @RequestBody request: UserSignUpApiRequestDto
    ): ResponseEntity<SuccessResponse<Token>>
}
