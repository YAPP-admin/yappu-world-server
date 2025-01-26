package co.yappuworld.user.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.presentation.dto.request.CheckingEmailAvailabilityApiRequestDto
import co.yappuworld.user.presentation.dto.request.LatestSignUpApplicationApiRequestDto
import co.yappuworld.user.presentation.dto.request.LoginApiRequestDto
import co.yappuworld.user.presentation.dto.request.ReissueTokenApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserSignUpApiRequestDto
import co.yappuworld.user.presentation.dto.response.LatestSignUpApplicationApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 인증 API", description = "회원가입, 로그인, 로그아웃 등..")
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
                                name = "가입 코드를 통해 별도 신청 절차 없이 가입 처리",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "accessToken": "accessToken...",
                                            "refreshToken": "refreshToken..."
                                        }
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "가입 코드 미입력 시, 가입 신청 처리",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": null
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
                                name = "가입코드 오류",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "errorCode": "USR_1001",
                                        "message": "잘못된 가입코드입니다."
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
        @Valid @RequestBody request: UserSignUpApiRequestDto
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "로그인")
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
                description = "로그인 실패",
                responseCode = "404",
                useReturnTypeSchema = true,
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
                description = "로그인 실패",
                responseCode = "409",
                useReturnTypeSchema = true,
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
        @Valid @RequestBody request: LoginApiRequestDto
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "토큰 재발급")
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
                                name = "토큰 재발급 성공",
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
                description = "로그인 실패",
                responseCode = "404",
                useReturnTypeSchema = true,
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
                description = "토큰 오류",
                responseCode = "409",
                useReturnTypeSchema = true,
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
        @RequestBody request: ReissueTokenApiRequestDto
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "이메일 중복 검사")
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
                                name = "토큰 재발급 성공",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": null
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                description = "중복된 이메일입니다.",
                responseCode = "409",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "중복된 이메일입니다.",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "중복된 이메일입니다.",
                                        "errorCode": "USR_1005"
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
        @Valid @RequestBody request: CheckingEmailAvailabilityApiRequestDto
    ): ResponseEntity<SuccessResponse<Unit>>

    @Operation(summary = "가장 최근의 회원가입 신청 조회")
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
                description = "가입 시 입력한 계정 정보와 일치하지 않습니다. (비밀번호 불일치)",
                responseCode = "403",
                useReturnTypeSchema = true,
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
                useReturnTypeSchema = true,
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
    @GetMapping("/v1/auth/applications/latest")
    fun findLatestSignUpApplication(
        @Valid @RequestBody request: LatestSignUpApplicationApiRequestDto
    ): ResponseEntity<SuccessResponse<LatestSignUpApplicationApiResponseDto>>
}
