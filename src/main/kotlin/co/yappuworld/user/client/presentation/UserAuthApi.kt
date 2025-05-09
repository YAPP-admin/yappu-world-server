package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.user.client.dto.request.CheckingEmailAvailabilityRequest
import co.yappuworld.user.client.dto.request.LatestSignUpApplicationRequest
import co.yappuworld.user.client.dto.request.LoginRequest
import co.yappuworld.user.client.dto.request.ReissueTokenRequest
import co.yappuworld.user.client.dto.request.UserSignUpRequest
import co.yappuworld.user.client.dto.response.LatestSignUpApplicationResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 인증 API", description = "회원가입, 로그인, 로그아웃 등")
interface UserAuthApi {

    @Operation(summary = "회원가입")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "가입 코드를 통해 별도 신청 절차 없이 가입 처리",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = Token::class),
                        examples = [
                            ExampleObject(
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
                description = "가입 코드를 입력하지 않는 경우, 가입 신청 처리",
                responseCode = "201",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "가입코드 오류",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "errorCode": "USR_1001",
                                        "message": "잘못된 가입코드입니다."
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "회원가입 중복 요청 오류",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "errorCode": "USR_1098",
                                        "message": "이미 처리 중인 이메일입니다."
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "이미 가입된 이메일",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "errorCode": "USR_1002",
                                        "message": "이미 가입된 이메일입니다."
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "처리되지 않은 회원가입 신청이 존재하는 경우",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "errorCode": "USR_1003",
                                        "message": "처리되지 않은 가입 신청이 존재하여, 추가 가입 신청이 불가합니다."
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
        @Valid @RequestBody request: UserSignUpRequest
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "로그인")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "로그인 성공",
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
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "이메일과 매칭되는 유저 정보를 찾을 수 없음",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "계정 정보를 찾을 수 없습니다.",
                                        "errorCode": "USR_1101"
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "회원가입 처리가 진행 중입니다",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "회원가입 처리가 진행 중입니다.",
                                        "errorCode": "USR_1102"
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "최근의 회원가입 신청은 거절되었습니다.",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "최근의 회원가입 신청은 거절되었습니다.",
                                        "errorCode": "USR_1103"
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "회원가입 승인은 되었으나, 유저 데이터가 생성되지 않음.",
                                description = "해당 예외는 서버 에러이므로 노티 필요",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "로그인이 불가능한 회원 상태입니다.",
                                        "errorCode": "USR_1104"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/v1/auth/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "토큰 재발급")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
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
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "토큰 정보와 매칭되는 유저를 찾을 수 없습니다.",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "계정 정보를 찾을 수 없습니다.",
                                        "errorCode": "USR_1101"
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "비정상 토큰",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "비정상 토큰입니다.",
                                        "errorCode": "TKN_0002"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/v1/auth/reissue-token")
    fun reissueToken(
        @RequestBody request: ReissueTokenRequest
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "이메일 중복 검사")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "이미 가입된 이메일",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "이미 가입된 이메일입니다.",
                                        "errorCode": "USR_1002"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/v1/auth/check-email")
    fun checkEmailAvailability(
        @Valid @RequestBody request: CheckingEmailAvailabilityRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "가장 최근의 회원가입 신청 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = LatestSignUpApplicationResponse::class),
                        examples = [
                            ExampleObject(
                                name = "최근의 가입 신청이 보류",
                                value = """
                                    {
                                         "isSuccess": "true",
                                        "data": {
                                            "status": "PENDING",
                                            "reason": null
                                        }
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "최근의 가입 신청이 거절",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "status": "REJECTED",
                                            "reason": "회원 여부를 증명할 수 없습니다."
                                        }
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "최근의 가입 신청이 승인",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "status": "APPROVED",
                                            "reason": null
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "가입 시 입력한 계정 정보와 일치하지 않습니다.",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "가입 시 입력한 계정 정보와 일치하지 않습니다.",
                                        "errorCode": "USR_1121"
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                description = "회원가입 신청을 한 내역이 없습니다.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "회원가입 신청을 한 내역이 없습니다.",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "회원가입 신청을 한 내역이 없습니다.",
                                        "errorCode": "USR_1122"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/v1/auth/applications/latest")
    fun findLatestSignUpApplication(
        @Valid @RequestBody request: LatestSignUpApplicationRequest
    ): ResponseEntity<SuccessResponse<LatestSignUpApplicationResponse>>

    @Operation(summary = "회원탈퇴")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "409",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "이미 탈퇴한 계정입니다.",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "이미 탈퇴한 계정입니다.",
                                        "errorCode": "USR_1201"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @DeleteMapping("/v1/auth/user")
    fun withdrawUser(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<Unit>
}
