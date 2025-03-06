package co.yappuworld.user.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.presentation.dto.request.AdminUserPageApiRequestDto
import co.yappuworld.user.presentation.dto.request.AdminUserUpdateApiRequestDto
import co.yappuworld.user.presentation.dto.response.AdminUserDetailsApiResponseDto
import co.yappuworld.user.presentation.dto.response.AdminUserOverviewApiResponseDto
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
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "회원 관리 API", description = "회원 조회 등..")
interface UserAdminApi {

    @Operation(summary = "유저 상세 조회")
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
                                name = "유저 상세 조회",
                                value = """
                                    {
                                        "data": {
                                            "userId": "01954c67-0c2b-d741-4561-ed80b4c28d0c",
                                            "name": "홍길동",
                                            "email": "email@email.com",
                                            "role": {
                                                "name": "ADMIN",
                                                "label": "관리자"
                                            },
                                            "isActive": true,
                                            "activityUnits": [
                                                {
                                                  "generation": 1,
                                                  "position": "PM"
                                                }
                                            ]
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
                description = "리소스를 찾을 수 없습니다.",
                responseCode = "404",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저가 존재하지 않습니다.",
                                value = """
                                    {
                                        "isSuccess": false,
                                        "message": "유저가 존재하지 않습니다.",
                                        "errorCode": "USR_0001"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @GetMapping("/admin/v1/users/{userId}")
    fun getUserDetails(
        @PathVariable("userId") userId: UUID
    ): ResponseEntity<SuccessResponse<AdminUserDetailsApiResponseDto>>

    @Operation(summary = "유저 목록 조회")
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
                                name = "유저 목록 조회",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "userId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                                                    "name": "홍길동",
                                                    "email": "abc@abc.com",
                                                    "role": {
                                                        "name": "ADMIN",
                                                        "label": "어드민"
                                                    },
                                                    "lastActivityUnit": {
                                                        "generation": 2,
                                                        "position": "PM"
                                                    }
                                                }
                                            ],
                                            "totalCount": 1,
                                            "page": 1,
                                            "size": 10,
                                            "totalPage": 1
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
    @GetMapping("/admin/v1/users")
    fun getUsers(
        @Valid @ParameterObject request: AdminUserPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminUserOverviewApiResponseDto>>>

    @Operation(
        summary = "유저 정보 변경",
        description = "유저 활동 내역의 경우 다음의 규칙에 따라 진행해주시면 됩니다.\n" +
            "1. 신규 -> ID 필드가 없거나 null 값으로 요청을 보내면 됩니다.\n" +
            "2. 수정 -> ID 필드를 유지하고 값을 수정하여 보내면 됩니다.\n" +
            "3. 삭제 -> 해당 원쇄를 아예 통으로 날리고 보내면 됩니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "리소스를 찾을 수 없습니다.",
                responseCode = "404",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = SuccessResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저가 존재하지 않습니다.",
                                value = """
                                    {
                                        "isSuccess": false,
                                        "message": "유저가 존재하지 않습니다.",
                                        "errorCode": "USR_0001"
                                    }
                                """
                            )
                        ]
                    )
                ]
            )
        ]
    )
    @PutMapping("/admin/v1/users")
    fun updateUserDetails(
        @Valid @RequestBody request: AdminUserUpdateApiRequestDto
    )
}
