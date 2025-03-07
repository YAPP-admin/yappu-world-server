package co.yappuworld.operation.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.presentation.dto.request.AdminGenerationPageApiRequestDto
import co.yappuworld.operation.presentation.dto.request.AdminGenerationRegisterApiRequestDto
import co.yappuworld.operation.presentation.dto.response.AdminGenerationApiResponseDto
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "어드민 유저 관련 운영 데이터 API")
interface AdminUserOperationApi {

    @Operation(summary = "기수 목록")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = [
                    Content(
                        schema = Schema(implementation = OffsetPageResponse::class),
                        examples = [
                            ExampleObject(
                                name = "직군 데이터 조회",
                                value = """
                                    {
                                        "data": {
                                            "data": [
                                                {
                                                    "generation": 24,
                                                    "startDate": "2024-05-03",
                                                    "endDate": "2024-09-14",
                                                    "isActive": false
                                                }
                                            ],
                                            "totalCount": 2,
                                            "totalPages": 2,
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
    @GetMapping("/admin/v1/operations/generations")
    fun getGenerations(
        @ParameterObject request: AdminGenerationPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminGenerationApiResponseDto>>>

    @Operation(summary = "신규 기수 등록")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "성공",
                responseCode = "200",
                useReturnTypeSchema = true,
                content = []
            )
        ]
    )
    @PostMapping("/admin/v1/operations/generations")
    fun registerGeneration(
        @Valid @RequestBody request: AdminGenerationRegisterApiRequestDto
    ): ResponseEntity<Unit>
}
