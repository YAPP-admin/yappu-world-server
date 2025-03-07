package co.yappuworld.operation.presentation

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.global.response.SuccessResponse
import co.yappuworld.operation.application.AdminUserOperationService
import co.yappuworld.operation.presentation.dto.request.AdminGenerationPageApiRequestDto
import co.yappuworld.operation.presentation.dto.request.AdminGenerationRegisterApiRequestDto
import co.yappuworld.operation.presentation.dto.response.AdminGenerationApiResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
class AdminUserOperationController(
    private val adminUserOperationService: AdminUserOperationService
) : AdminUserOperationApi {

    override fun getGenerations(
        request: AdminGenerationPageApiRequestDto
    ): ResponseEntity<SuccessResponse<OffsetPageResponse<AdminGenerationApiResponseDto>>> {
        val response = adminUserOperationService.getGenerations(request.toAppRequest())
        return ResponseEntity.ok(
            SuccessResponse(
                OffsetPageResponse(
                    data = response.generations.map { AdminGenerationApiResponseDto(it) },
                    totalCount = response.totalElements,
                    totalPages = response.totalPages,
                    page = request.page,
                    size = request.size
                )
            )
        )
    }

    override fun registerGeneration(request: AdminGenerationRegisterApiRequestDto): ResponseEntity<Unit> {
        adminUserOperationService.registerGeneration(request.toAppRequest())
        return ResponseEntity.created(URI("/admin/v1/operations")).build()
    }
}
