package co.yappuworld.user.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import co.yappuworld.user.presentation.dto.request.UserRoleUpdateApiRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "회원 인증/인가 API", description = "회원가입 신청 승인, 거절 등..")
interface UserAuthAdminApi {

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
}
