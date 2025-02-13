package co.yappuworld.external.discord

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "discord")
data class DiscordProperty(
    val webhook: String
)
