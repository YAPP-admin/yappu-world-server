package co.yappuworld.operation.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.schedule.domain.vo.AttendanceError
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GenerationFindService(
    private val generationRepository: GenerationRepository
) {

    fun findActiveGenerationOrNull(): Int? = generationRepository.getGenerationOrNullByIsActiveIsTrue()?.value

    fun findActiveGeneration(): Int =
        generationRepository.getGenerationOrNullByIsActiveIsTrue()?.value
            ?: throw BusinessException(AttendanceError.NO_ACTIVE_GENERATION)

    fun findGenerations(values: List<Int>): List<GenerationEntity> = generationRepository.findAllByValueIn(values)
}
