package co.yappuworld.operation.client.presentation

import co.yappuworld.global.response.ErrorResponse
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.client.dto.request.AdminGenerationActiveUpdateRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationPageRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationRegisterRequest
import co.yappuworld.operation.client.dto.request.AdminSignupCodeDeleteRequest
import co.yappuworld.operation.client.dto.response.AdminGenerationActiveUpdateResponse
import co.yappuworld.operation.client.dto.response.AdminGenerationResponse
import co.yappuworld.user.client.dto.request.AdminSignUpCodeUpdateRequest
import co.yappuworld.user.client.dto.response.AdminSignUpCodesResponse
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
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "회원 운영 API", description = "_")
interface AdminUserOperationApi {

    @Operation(summary = "기수 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "직군 데이터 조회",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "generation": 25,
                                                    "startDate": "2024-05-03",
                                                    "endDate": "2024-09-14",
                                                    "isActive": false
                                                },
                                                {
                                                    "generation": 3,
                                                    "startDate": null,
                                                    "endDate": null,
                                                    "isActive": false
                                                }
                                            ],
                                            "totalCount": 3,
                                            "totalPages": 2,
                                            "page": 1,
                                            "size": 2
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
    @GetMapping("/admin/v1/operations/generations")
    fun getGenerations(
        @ParameterObject request: AdminGenerationPageRequest
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminGenerationResponse>>>

    @Operation(summary = "신규 기수 등록")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "201",
                content = [Content()]
            )
        ]
    )
    @PostMapping("/admin/v1/operations/generations")
    fun registerGeneration(
        @Valid @RequestBody request: AdminGenerationRegisterRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "기수 활성화 상태 변경")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        examples = [
                            ExampleObject(
                                name = "활성화 된 기수가 없을 때 활성화",
                                value = """
                                    {
                                        "data": {
                                            "activatedGeneration": 25,
                                            "deactivatedGeneration": null
                                        },
                                        "isSuccess": true
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "활성화 된 기수가 있을 때 활성화",
                                value = """
                                    {
                                        "data": {
                                            "activatedGeneration": 25,
                                            "deactivatedGeneration": 24
                                        },
                                        "isSuccess": true
                                    }
                                """
                            ),
                            ExampleObject(
                                name = "특정 기수 비활성화",
                                value = """
                                    {
                                        "data": {
                                            "activatedGeneration": null,
                                            "deactivatedGeneration": 24
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
                content = [
                    Content(
                        schema = Schema(implementation = ErrorResponse::class),
                        examples = [
                            ExampleObject(
                                name = "존재하지 않는 기수",
                                value = """
                                    {
                                        "errorCode": "OPR_1000",
                                        "message": "존재하지 않는 기수입니다.",
                                        "isSuccess": false
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
                                name = "활성화 정합성 오류",
                                value = """
                                    {
                                        "errorCode": "OPR_1001",
                                        "message": "기수 활성상태의 정합성이 맞지 않습니다. 데이터를 확인해주세요.",
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
    @PatchMapping("/admin/v1/operations/generations/active")
    fun updateActiveGeneration(
        @Valid @RequestBody request: AdminGenerationActiveUpdateRequest
    ): ResponseEntity<SuccessResponse<AdminGenerationActiveUpdateResponse>>

    @Operation(summary = "회원가입 인증번호 조회")
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
                                        "data": {
                                            "codes": [
                                                {
                                                  "code": "000000",
                                                  "role": {
                                                    "name": "ADMIN",
                                                    "label": "관리자"
                                                  }
                                                },
                                                {
                                                  "code": "000001",
                                                  "role": {
                                                    "name": "STAFF",
                                                    "label": "운영진"
                                                  }
                                                },
                                                {
                                                  "code": "000002",
                                                  "role": {
                                                    "name": "ALUMNI",
                                                    "label": "정회원"
                                                  }
                                                },
                                                {
                                                  "code": "",
                                                  "role": {
                                                    "name": "GRADUATE",
                                                    "label": "수료회원"
                                                  }
                                                },
                                                {
                                                  "code": "000003",
                                                  "role": {
                                                    "name": "ACTIVE",
                                                    "label": "활동회원"
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
            )
        ]
    )
    @GetMapping("/admin/v1/auth/authentication-codes")
    fun getSignUpAuthenticationCode(): ResponseEntity<SuccessResponse<AdminSignUpCodesResponse>>

    @Operation(summary = "인증번호 수정")
    @ApiResponse(
        responseCode = "204",
        content = [Content()]
    )
    @PatchMapping("/admin/v1/auth/authentication-codes")
    fun updateSignUpAuthenticationCode(
        @Valid @RequestBody request: AdminSignUpCodeUpdateRequest
    ): ResponseEntity<Unit>

    @Operation(summary = "인증번호 초기화")
    @ApiResponse(
        responseCode = "204",
        content = [Content()]
    )
    @DeleteMapping("/admin/v1/auth/authentication-codes")
    fun deleteSignUpAuthenticationCode(
        @Valid @RequestBody request: AdminSignupCodeDeleteRequest
    ): ResponseEntity<Unit>
}
