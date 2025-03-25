package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.GenerationEntity
import org.springframework.data.jpa.repository.JpaRepository

interface GenerationJpaRepository : JpaRepository<GenerationEntity, Int> {

    fun existsGenerationByIsActiveIsTrue(): Boolean

    fun getGenerationOrNullByIsActiveIsTrue(): GenerationEntity?

    fun findAllByIsActiveIsTrue(): List<GenerationEntity>

    fun findAllByValueIn(values: List<Int>): List<GenerationEntity>
}
