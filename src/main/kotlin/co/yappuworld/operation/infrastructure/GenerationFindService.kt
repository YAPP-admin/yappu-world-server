package co.yappuworld.operation.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.schedule.domain.vo.AttendanceError
import org.springframework.data.domain.Sort
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

    fun existsGeneration(value: Int): Boolean = generationRepository.existsById(value)

    fun existsActiveGeneration(): Boolean =
        generationRepository
            .findAll(limit = 1) {
                select(intLiteral(1))
                    .from(entity(GenerationEntity::class))
                    .where(path(GenerationEntity::isActive).equal(true))
            }.isNotEmpty()

    fun findGenerations(values: List<Int>): List<GenerationEntity> = generationRepository.findAllByValueIn(values)

    fun findAllGenerations(): List<GenerationEntity> =
        generationRepository.findAll(Sort.by(Sort.Direction.DESC, "value"))

    fun findAllActiveGeneration(): List<GenerationEntity> =
        generationRepository
            .findAll {
                select(entity(GenerationEntity::class))
                    .from(entity(GenerationEntity::class))
                    .where(path(GenerationEntity::isActive).equal(true))
            }.filterNotNull()
}
