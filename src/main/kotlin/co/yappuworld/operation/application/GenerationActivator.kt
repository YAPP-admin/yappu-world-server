package co.yappuworld.operation.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.application.dto.param.GenerationActivationControlResult
import co.yappuworld.operation.domain.Generation
import co.yappuworld.operation.domain.OperationError
import co.yappuworld.operation.infrastructure.GenerationRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger { }

@Component
class GenerationActivator(
    private val generationRepository: GenerationRepository
) {

    @Transactional
    fun activate(targetGenerationValue: Int): GenerationActivationControlResult {
        // 기존에 활성화 되어 있는 기수를 비활성화 시키고 활성화를 진행합니다.
        val deactivatedGeneration: Int? = deactivateGeneration()
        val targetGeneration = generationRepository.findByIdOrNull(targetGenerationValue)
            ?: throw BusinessException(OperationError.NOT_EXIST_GENERATION)

        return targetGeneration
            .apply { activate() }
            .also { generationRepository.save(it) }
            .let { GenerationActivationControlResult(it.value, deactivatedGeneration) }
    }

    @Transactional
    fun deactivate(targetGenerationValue: Int): GenerationActivationControlResult =
        GenerationActivationControlResult(
            deactivatedGeneration = deactivateGeneration(targetGenerationValue)
        )

    private fun deactivateGeneration(targetGenerationValue: Int? = null): Int? {
        if (!generationRepository.existsGenerationByIsActiveIsTrue()) return null

        val activeGenerations = generationRepository.findAllByIsActiveIsTrue()
        checkDeactivatingConsistency(activeGenerations, targetGenerationValue)

        return activeGenerations
            .single()
            .apply { deactivate() }
            .also { generationRepository.save(it) }
            .value
    }

    private fun checkDeactivatingConsistency(
        activeGenerations: List<Generation>,
        deactivateTargetGeneration: Int? = null
    ) {
        if (deactivateTargetGeneration != null && activeGenerations.isEmpty()) {
            logger.error {
                """
                활성화 된 기수는 없으나, 비활성화 요청이 들어왔습니다.
                비활성화 요청 기수: $deactivateTargetGeneration
                """.trimIndent()
            }
            throw BusinessException(OperationError.GENERATION_INCONSISTENCY)
        }

        if (activeGenerations.size > 1) {
            logger.error { "활성화 된 기수: ${activeGenerations.map { it.value }.joinToString(", ")}}" }
            throw BusinessException(OperationError.GENERATION_INCONSISTENCY)
        }

        val activeGeneration = activeGenerations.single()
        if (deactivateTargetGeneration != null && deactivateTargetGeneration != activeGeneration.value) {
            logger.error {
                """
                    활성화 해제 목표: $deactivateTargetGeneration
                    현재 활성화 된 기수: ${activeGeneration.value}
                """.trimIndent()
            }
            throw BusinessException(OperationError.GENERATION_INCONSISTENCY)
        }
    }
}
