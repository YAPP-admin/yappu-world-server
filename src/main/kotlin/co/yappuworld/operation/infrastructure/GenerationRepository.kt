package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.GenerationEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository

interface GenerationRepository :
    JpaRepository<GenerationEntity, Int>,
    KotlinJdslJpqlExecutor {

    fun getGenerationOrNullByIsActiveIsTrue(): GenerationEntity?

    fun findAllByValueIn(values: List<Int>): List<GenerationEntity>
}
