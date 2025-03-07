package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.Generation
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface GenerationRepository :
    CrudRepository<Generation, Int>,
    PagingAndSortingRepository<Generation, Int>
