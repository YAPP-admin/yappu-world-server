package co.yappuworld.external.messenger

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "discord")
data class DiscordProperties(
    val webhook: String
)
