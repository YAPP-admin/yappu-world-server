package co.yappuworld.user.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.presentation.dto.request.SignUpApplicationApproveApiRequestDto
import co.yappuworld.user.presentation.dto.request.SignUpApplicationRejectApiRequestDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "유저 인증 어드민 API", description = "회원가입 신청 승인, 거절 등..")
interface UserAuthAdminApi {

    @Operation(summary = "회원가입 신청 승인")
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
                                name = "신청 승인이 정상 처리",
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
    @PostMapping("/v1/admin/auth/application/approve")
    fun approveSignUpApplication(
        @RequestBody request: SignUpApplicationApproveApiRequestDto
    ): ResponseEntity<SuccessResponse<Unit>>

    @Operation(summary = "회원가입 신청 반려")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "신청 승인이 정상 처리",
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
    @PostMapping("/v1/admin/auth/application/reject")
    fun rejectSignUpApplication(
        @RequestBody request: SignUpApplicationRejectApiRequestDto
    ): ResponseEntity<SuccessResponse<Unit>>
}
