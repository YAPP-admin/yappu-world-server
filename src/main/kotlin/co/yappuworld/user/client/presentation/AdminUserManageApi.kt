package co.yappuworld.user.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.user.client.dto.request.AdminUserPageRequest
import co.yappuworld.user.client.dto.request.AdminUserUpdateRequest
import co.yappuworld.user.client.dto.response.AdminUserDetailResponse
import co.yappuworld.user.client.dto.response.AdminUserOverviewResponse
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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.UUID

@Tag(name = "회원 관리 API", description = "_")
interface AdminUserManageApi {

    @Operation(summary = "유저 상세 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                content = [
                    Content(
                        schema = Schema(implementation = AdminUserDetailResponse::class),
                        examples = [
                            ExampleObject(
                                name = "유저 상세 조회",
                                value = """
                                    {
                                        "data": {
                                            "id": "01954809-38fd-1268-e0d6-d3fda39f6b4c",
                                            "name": "홍길동",
                                            "email": "admin@admin.com",
                                            "phoneNumber": null,
                                            "gender": null,
                                            "role": "관리자",
                                            "isActive": true,
                                            "registrationDate": "2025-03-05",
                                            "activityUnits": [
                                                {
                                                    "id": "7f1080cb-0579-11f0-bb9e-0242ac120003",
                                                    "generation": 25,
                                                    "position": "PM",
                                                    "isActive": true,
                                                    "team": {
                                                      "id": "a1b2c3d4-e5f6-7890-ab12-cd34ef56gh78",
                                                      "name": "야뿌월드 1팀"
                                                    }
                                                },
                                                {
                                                    "id": "7f1080cb-0579-11f0-bb9e-0242ac120002",
                                                    "generation": 1,
                                                    "position": "PM",
                                                    "isActive": false,
                                                    "team": {
                                                      "id": "7f1080cb-0579-11f0-bb9e-0242ac120002",
                                                      "name": "야뿌월드 2팀"
                                                    }
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
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
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
    fun getUserDetail(
        @PathVariable("userId") userId: UUID
    ): ResponseEntity<SuccessResponse<AdminUserDetailResponse>>

    @Operation(summary = "유저 목록 조회")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
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
        @Valid @ParameterObject request: AdminUserPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminUserOverviewResponse>>>

    @Operation(
        summary = "유저 정보 변경",
        description = """
            유저 활동 내역의 경우 다음의 규칙에 따라 진행해주시면 됩니다.
            1. 신규 -> ID 필드를 NULL 값으로 하여 요청을 보내면 됩니다.
            2. 수정 -> ID 필드를 유지하고 값을 수정하여 보내면 됩니다.
            3. 삭제 -> 해당 활동 이력을 제외하고 보내면 됩니다.
        """
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                description = "리소스를 찾을 수 없습니다.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
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
        @Valid @RequestBody request: AdminUserUpdateRequest
    ): ResponseEntity<Unit>

    @Operation(
        summary = "회원 탈퇴 처리",
        description = "_"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                content = [Content()]
            ),
            ApiResponse(
                description = "리소스를 찾을 수 없습니다.",
                responseCode = "404",
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
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
    @DeleteMapping("/admin/v1/users/{userId}")
    fun deactivateUser(
        @PathVariable("userId") userId: UUID
    ): ResponseEntity<Unit>
}
