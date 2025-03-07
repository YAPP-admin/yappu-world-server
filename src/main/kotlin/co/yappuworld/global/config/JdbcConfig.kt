package co.yappuworld.global.config

import co.yappuworld.global.persistence.UserApplicationReadingConverter
import co.yappuworld.global.persistence.UserApplicationWritingConverter
import co.yappuworld.global.persistence.UuidIdentifierReadingConverter
import co.yappuworld.global.persistence.UuidIdentifierWritingConverter
import co.yappuworld.operation.domain.Generation
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories
import org.springframework.data.relational.core.mapping.event.AfterConvertEvent
import org.springframework.data.relational.core.mapping.event.AfterSaveEvent

@Configuration
@EnableJdbcAuditing
@EnableJdbcRepositories(basePackages = ["co.yappuworld.**.infrastructure"])
class JdbcConfig : AbstractJdbcConfiguration() {

    override fun jdbcCustomConversions(): JdbcCustomConversions =
        JdbcCustomConversions(
            listOf(
                UuidIdentifierWritingConverter(),
                UuidIdentifierReadingConverter(),
                UserApplicationWritingConverter(),
                UserApplicationReadingConverter()
            )
        )

    @Bean
    fun onAfterSaveGeneration(): ApplicationListener<AfterSaveEvent<Generation>> =
        ApplicationListener { e ->
            e.entity.load()
        }

    @Bean
    fun onAfterLoadGeneration(): ApplicationListener<AfterConvertEvent<Generation>> =
        ApplicationListener { e ->
            e.entity.load()
        }
}
