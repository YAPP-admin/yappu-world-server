package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.client.dto.response.AdminUserProfileResponse
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "어드민 회원 API", description = "_")
interface AdminUserApi {

    @GetMapping("/admin/v1/users/profile")
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
                                name = "유저 목록 조회",
                                value = """
                                    {
                                        "data": {
                                            "id": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                            "name": "홍길동",
                                            "role": "관리자",
                                            "position": "PM"
                                        },
                                        "isSuccess": true
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
                                name = "유저 정보 찾을 수 없음",
                                value = """
                                    {
                                        "message": "유저를 찾을 수 없습니다.",
                                        "errorCode": "USR_0001",
                                        "isSuccess": true
                                    }
                                """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "500",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "서버 오류",
                                value = """
                                    {
                                        "message": "유저 조회에 에러가 발생했습니다.",
                                        "errorCode": "USR_0005",
                                        "isSuccess": false
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    fun getUserProfile(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<AdminUserProfileResponse>>
}
