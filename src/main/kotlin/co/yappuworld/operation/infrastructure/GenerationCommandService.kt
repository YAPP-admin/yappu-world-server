package co.yappuworld.operation.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.OperationError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GenerationCommandService(
    private val generationRepository: GenerationRepository
) {

    fun deleteAll(generations: List<Int>) {
        val generationEntities = generationRepository.findAllByValueIn(generations)
        if (generationEntities.size != generations.size) {
            throw BusinessException(OperationError.CONTAIN_NOT_EXIST_GENERATION)
        }

        generationRepository.deleteAll(generationEntities)
    }
}
