package co.yappuworld.global.config

import co.yappuworld.global.persistence.UserApplicationReadingConverter
import co.yappuworld.global.persistence.UserApplicationWritingConverter
import co.yappuworld.global.persistence.UuidIdentifierReadingConverter
import co.yappuworld.global.persistence.UuidIdentifierWritingConverter
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories

@Configuration
@EnableJdbcRepositories(basePackages = ["co.yappuworld.**.infrastructure"])
@EnableJdbcAuditing
class JdbcConfig : AbstractJdbcConfiguration() {

    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(
            listOf(
                UuidIdentifierWritingConverter(),
                UuidIdentifierReadingConverter(),
                UserApplicationWritingConverter(),
                UserApplicationReadingConverter()
            )
        )
    }
}
