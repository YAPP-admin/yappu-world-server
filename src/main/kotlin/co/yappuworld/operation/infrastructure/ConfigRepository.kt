package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.domain.ConfigEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConfigRepository : JpaRepository<ConfigEntity, String> {

    fun findAllByIdIn(ids: List<String>): List<ConfigEntity>

    fun findByCategory(category: ConfigCategory): List<ConfigEntity>
}
