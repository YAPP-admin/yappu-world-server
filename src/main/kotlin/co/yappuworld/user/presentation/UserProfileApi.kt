package co.yappuworld.user.presentation

import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.user.presentation.dto.response.UserProfileApiResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping

@Tag(name = "유저 프로필 API", description = "프로필 조회, 수정 등..")
interface UserProfileApi {

    @Operation(summary = "프로필 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "프로필 조회 성공",
                                value = """
                                    {
                                        "isSuccess": "true",
                                        "data": {
                                            "id": "79d52d77-6123-40f1-9f70-64bcbd6ca21a",
                                            "name": "홍길동",
                                            "email": "abc@abc.com",
                                            "activityUnits": [
                                                {
                                                    "generation": "1",
                                                    "position": "PM"
                                                },
                                                {
                                                    "generation": "2",
                                                    "position": "ANDROID"
                                                }
                                            ]
                                        }
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/users/profile")
    fun getProfile(
        @AuthenticationPrincipal securityUser: SecurityUser
    ): ResponseEntity<SuccessResponse<UserProfileApiResponseDto>>
}
