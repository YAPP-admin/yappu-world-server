package co.yappuworld.operation.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.application.dto.request.AdminGenerationActiveUpdateAppRequestDto
import co.yappuworld.operation.application.dto.request.AdminGenerationPageAppRequestDto
import co.yappuworld.operation.application.dto.response.AdminGenerationActiveUpdateAppResponseDto
import co.yappuworld.operation.application.dto.response.AdminGenerationsAppParamDto
import co.yappuworld.operation.domain.Generation
import co.yappuworld.operation.domain.OperationError
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.operation.presentation.dto.request.AdminGenerationRegisterAppRequestDto
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AdminUserOperationService(
    private val generationActivator: GenerationActivator,
    private val generationRepository: GenerationRepository
) {

    @Transactional(readOnly = true)
    fun getGenerations(request: AdminGenerationPageAppRequestDto): AdminGenerationsAppParamDto {
        val pageResponse = generationRepository.findAll(request.toPageRequest())
        return AdminGenerationsAppParamDto(pageResponse)
    }

    @Transactional
    fun registerGeneration(request: AdminGenerationRegisterAppRequestDto) {
        if (request.isActive) {
            deactivateGeneration()
        }

        generationRepository.save(request.toDomain())
    }

    @Transactional
    fun updateGenerationActiveStatus(
        request: AdminGenerationActiveUpdateAppRequestDto
    ): AdminGenerationActiveUpdateAppResponseDto =
        AdminGenerationActiveUpdateAppResponseDto(
            when (request.targetActive) {
                true -> generationActivator.activate(request.generation)
                false -> generationActivator.deactivate(request.generation)
            }
        )

    private fun activateGeneration(generation: Int): Generation {
        if (generationRepository.existsGenerationByIsActiveIsTrue()) {
            deactivateGeneration()
        }

        return generationRepository
            .findByIdOrNull(generation)
            ?.apply { activate() }
            ?.also { generationRepository.save(it) }
            ?: throw BusinessException(OperationError.NOT_EXIST_GENERATION)
    }

    private fun deactivateGeneration(targetGeneration: Int? = null): Generation? {
        val activeGenerationsOrEmpty = generationRepository.findAllByIsActiveIsTrue()

        if (activeGenerationsOrEmpty.isEmpty()) return null
        if (activeGenerationsOrEmpty.size > 1) throw BusinessException(OperationError.GENERATION_INCONSISTENCY)

        val generation = activeGenerationsOrEmpty.single()

        if (targetGeneration != null && generation.value != targetGeneration) {
            throw BusinessException(OperationError.GENERATION_INCONSISTENCY)
        }

        return generation
            .apply { deactivate() }
            .also { generationRepository.save(it) }
    }

}
