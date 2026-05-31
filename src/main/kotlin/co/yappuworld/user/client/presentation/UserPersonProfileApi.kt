package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.client.dto.response.UserPersonProfileResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.UUID

@Tag(name = "공개 유저 프로필 API", description = "_")
interface UserPersonProfileApi {

    @Operation(summary = "공개 유저 프로필 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "공개 유저 프로필 조회 성공",
                                value = """
                                    {
                                        "isSuccess": true,
                                        "data": {
                                            "userId": "79d52d77-6123-40f1-9f70-64bcbd6ca21a",
                                            "name": "김뿌야",
                                            "role": "활동회원",
                                            "latestActivity": {
                                                "generation": 25,
                                                "position": "Design"
                                            },
                                            "histories": [
                                                {
                                                    "generation": 25,
                                                    "position": "Design",
                                                    "activityStartDate": "2023-11-01",
                                                    "activityEndDate": "2024-06-30",
                                                    "service": {
                                                        "serviceId": "a1b2c3d4-e5f6-7890-ab12-cd34ef56ab78",
                                                        "teamName": "팀 이름",
                                                        "serviceName": "서비스명",
                                                        "summary": "무수무수한 서비스 두줄까지 들어갈것 같아요",
                                                        "hasApp": true,
                                                        "hasWeb": true,
                                                        "googlePlayLink": "https://play.google.com/store/apps/details?id=com.example",
                                                        "appStoreLink": "https://apps.apple.com/app/id123456789",
                                                        "webLink": "https://example.com",
                                                        "thumbnailImageUrl": null
                                                    }
                                                }
                                            ]
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
                                name = "존재하지 않는 유저",
                                value = """
                                    {
                                        "isSuccess": false,
                                        "message": "사용자를 찾을 수 없습니다.",
                                        "errorCode": "USER_0001"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/v1/users/{userId}/profile")
    fun getUserPersonProfile(
        @PathVariable userId: UUID
    ): ResponseEntity<SuccessResponse<UserPersonProfileResponse>>
}
