package co.yappuworld.global.config

import co.yappuworld.global.persistence.UuidIdentifierReadingConverter
import co.yappuworld.global.persistence.UuidIdentifierWritingConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration
@EnableJdbcAuditing
@EnableJdbcRepositories(basePackages = ["co.yappuworld.**.infrastructure"])
class JdbcConfig : AbstractJdbcConfiguration() {

    override fun jdbcCustomConversions(): JdbcCustomConversions =
        JdbcCustomConversions(
            listOf(
                UuidIdentifierWritingConverter(),
                UuidIdentifierReadingConverter()
            )
        )
}
