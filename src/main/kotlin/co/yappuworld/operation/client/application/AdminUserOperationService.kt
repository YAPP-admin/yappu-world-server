package co.yappuworld.operation.client.application

import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.operation.client.dto.request.AdminGenerationActiveUpdateRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationDeleteRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationPageRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationRegisterRequest
import co.yappuworld.operation.client.dto.response.AdminGenerationActiveUpdateResponse
import co.yappuworld.operation.client.dto.response.AdminGenerationResponse
import co.yappuworld.operation.infrastructure.GenerationCommandService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.operation.infrastructure.GenerationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserOperationService(
    private val generationActiveStateManager: GenerationActiveStateManager,
    private val generationFindService: GenerationFindService,
    private val generationCommandService: GenerationCommandService,
    private val generationRepository: GenerationRepository
) {

    @Transactional(readOnly = true)
    fun getGenerations(request: AdminGenerationPageRequest): OffsetPageResponse<AdminGenerationResponse> =
        generationRepository.findAll(request.toPageRequest()).let {
            OffsetPageResponse(
                data = it.content.map { generation -> AdminGenerationResponse(generation) },
                totalCount = it.totalElements,
                totalPages = it.totalPages,
                page = request.page,
                size = request.size
            )
        }

    @Transactional
    fun registerGeneration(request: AdminGenerationRegisterRequest) {
        generationRepository.save(request.toDomain())
        if (request.isActive) {
            generationActiveStateManager.activate(request.generation)
        }
    }

    @Transactional
    fun updateGenerationActiveStatus(request: AdminGenerationActiveUpdateRequest): AdminGenerationActiveUpdateResponse =
        AdminGenerationActiveUpdateResponse(
            when (request.targetActive) {
                true -> generationActiveStateManager.activate(request.generation)
                false -> generationActiveStateManager.deactivate(request.generation)
            }
        )

    @Transactional
    fun deleteGenerations(request: AdminGenerationDeleteRequest) {
        generationCommandService.deleteAll(request.generations)
    }
}
