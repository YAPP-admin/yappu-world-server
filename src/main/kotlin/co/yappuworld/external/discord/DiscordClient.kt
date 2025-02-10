package co.yappuworld.external.discord

import co.yappuworld.external.discord.dto.DiscordEmbed
import co.yappuworld.external.discord.dto.DiscordMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

/**
 * message customizing: https://discord.com/developers/docs/interactions/message-components
 */
@Component
class DiscordClient(
    private val discordProperty: DiscordProperty
) {

    fun send(content: String) {
        val message = jacksonObjectMapper().writeValueAsString(DiscordMessage(content))
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        RestTemplate().postForObject(
            discordProperty.webhookUri,
            HttpEntity(message, headers),
            Unit::class.java
        )
    }

    fun send(embed: DiscordEmbed) {
        val message = jacksonObjectMapper().writeValueAsString(DiscordMessage.of(embed))
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }

        RestTemplate().postForObject(
            discordProperty.webhookUri,
            HttpEntity(message, headers),
            Unit::class.java
        )
    }
}
