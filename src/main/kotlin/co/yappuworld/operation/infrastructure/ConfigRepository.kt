package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.Config
import co.yappuworld.operation.domain.ConfigCategory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ConfigRepository : CrudRepository<Config, String> {

    fun findAllByIdIn(ids: List<String>): List<Config>

    fun findByCategory(category: ConfigCategory): List<Config>
}
