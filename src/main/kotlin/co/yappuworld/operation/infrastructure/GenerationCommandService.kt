package co.yappuworld.operation.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.OperationError
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GenerationCommandService(
    private val generationRepository: GenerationRepository
) {

    fun delete(generation: Int) {
        generationRepository
            .findByIdOrNull(generation)
            ?.also { generationRepository.delete(it) }
            ?: throw BusinessException(OperationError.NOT_EXIST_GENERATION)
    }
}
