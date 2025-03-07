package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.Generation
import org.springframework.data.repository.CrudRepository

interface GenerationRepository : CrudRepository<Generation, Int>
