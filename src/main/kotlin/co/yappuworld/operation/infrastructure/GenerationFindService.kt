package co.yappuworld.operation.infrastructure

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GenerationFindService(
    private val generationRepository: GenerationRepository
) {

    fun findActiveGeneration(): Int? = generationRepository.getGenerationOrNullByIsActiveIsTrue()?.value
}
