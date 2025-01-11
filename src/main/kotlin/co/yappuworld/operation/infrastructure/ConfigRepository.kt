package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.Config
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigRepository : CrudRepository<Config, String> {
    fun findAllByIdIn(ids: List<String>): List<Config>
}
