package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.response.OffsetPageResponse
import co.yappuworld.operation.client.dto.request.AdminGenerationActiveUpdateRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationDeleteRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationPageRequest
import co.yappuworld.operation.client.dto.request.AdminGenerationRegisterRequest
import co.yappuworld.operation.client.dto.response.AdminGenerationActiveUpdateResponse
import co.yappuworld.operation.client.dto.response.AdminGenerationResponse
import co.yappuworld.operation.domain.OperationError
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.GenerationCommandService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.user.client.dto.response.AdminSignUpCodeResponse
import co.yappuworld.user.client.dto.response.AdminSignUpCodesResponse
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserOperationService(
    private val generationActiveStateManager: GenerationActiveStateManager,
    private val configFindService: ConfigFindService,
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
        if (generationFindService.existsGeneration(request.generation)) {
            throw BusinessException(OperationError.EXISTS_GENERATION)
        }

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

    @Transactional(readOnly = true)
    fun getSignUpAuthenticationCodes(): AdminSignUpCodesResponse {
        val responseByKey = configFindService
            .findSignUpCodeConfigs()
            .associateBy { it.id }

        return AdminSignUpCodesResponse(
            UserRole.entries.map { role ->
                AdminSignUpCodeResponse(responseByKey[role.signUpCodeKey], role)
            }
        )
    }
}
