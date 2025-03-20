package co.yappuworld.user.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.Token
import co.yappuworld.user.application.dto.request.LoginRequest
import co.yappuworld.user.presentation.dto.request.AdminSignUpApplicationPageApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserRoleUpdateApiRequestDto
import co.yappuworld.user.presentation.dto.response.AdminSignUpApplicationApiResponseDto
import co.yappuworld.user.application.dto.response.AdminSignUpApplicationOverviewResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "회원 인증/인가 API", description = "회원 권한/가입신청 관리")
interface UserAuthAdminApi {

    @Operation(summary = "로그인")
    @ApiResponses(
        value = [
            ApiResponse(
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
                responseCode = "401",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "비밀번호 오류",
                                value = """
                                    {
                                        "isSuccess": "false",
                                        "message": "로그인에 실패했습니다. 계정 정보를 다시 확인하세요.",
                                        "errorCode": "USR_1105"
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
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
    @PostMapping("/admin/v1/auth/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<SuccessResponse<Token>>

    @Operation(summary = "유저 역할 변경")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "204",
                useReturnTypeSchema = true,
                content = [Content()]
            )
        ]
    )
    @PatchMapping("/admin/v1/users/role")
    fun updateUserRole(
        @Valid @RequestBody request: UserRoleUpdateApiRequestDto
    ): ResponseEntity<Unit>

    @Operation(summary = "회원가입 신청 승인")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "204",
                useReturnTypeSchema = true,
                content = [Content()]
            ),
            ApiResponse(
                description = "리소스를 찾을 수 없음",
                responseCode = "404",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "요청 ID로 신청서를 찾을 수 없음",
                                value = """
                                    {
                                        "isSuccess": false,
                                        "message": "회원가입 신청 내역을 찾을 수 없습니다.",
                                        "errorCode": "USR_1099"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/admin/v1/auth/applications/approve")
    fun approveSignUpApplication(
        @RequestBody request: SignUpApplicationApproveApiRequestDto
    ): ResponseEntity<Unit>

    @Operation(summary = "회원가입 신청 반려")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                description = "리소스를 찾을 수 없음",
                responseCode = "404",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "요청 ID로 신청서를 찾을 수 없음",
                                value = """
                                    {
                                        "isSuccess": false,
                                        "message": "회원가입 신청 내역을 찾을 수 없습니다.",
                                        "errorCode": "USR_1099"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PostMapping("/admin/v1/auth/applications/reject")
    fun rejectSignUpApplication(
        @RequestBody request: SignUpApplicationRejectApiRequestDto
    ): ResponseEntity<Unit>

    @Operation(summary = "회원가입 신청서 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "data": {
                                                "applicationId": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                                "name": "홍길동",
                                                "email": "email@email.com",
                                                "applicationDate": "2025-03-04",
                                                "activityUnit": {
                                                    "generation": 1,
                                                    "position": {
                                                        "name": "PM",
                                                        "label": "PM"
                                                    }
                                                },
                                                "status": "대기"
                                            },
                                            "totalCount": 3,
                                            "page": 1,
                                            "size": 1
                                        },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/admin/v1/auth/applications")
    fun getSignUpApplications(
        @Valid @ParameterObject request: AdminSignUpApplicationPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminSignUpApplicationOverviewResponse>>>

    @Operation(summary = "회원가입 신청서 상세")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                value = """
                                    {
                                        "data": {
                                            "details": {
                                                "name": "홍길동",
                                                "email": "email@email.com",
                                                "applicationDate": "2025-03-04",
                                                "activityUnits": [
                                                    {
                                                        "generation": 1,
                                                        "position": {
                                                            "name": "PM",
                                                            "label": "PM"
                                                        }
                                                    }
                                                ]
                                            },
                                            "status": "대기",
                                            "rejectReason": null,
                                            "assignedRole": null
                                            },
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/admin/v1/auth/applications/{applicationId}")
    fun getSignUpApplication(
        @PathVariable applicationId: UUID
    ): ResponseEntity<SuccessResponse<AdminSignUpApplicationApiResponseDto>>
}
