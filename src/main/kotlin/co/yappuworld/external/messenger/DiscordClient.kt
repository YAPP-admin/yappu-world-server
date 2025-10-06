package co.yappuworld.external.messenger

import co.yappuworld.external.messenger.dto.DiscordMessage
import co.yappuworld.external.messenger.dto.DiscordEmbed
import co.yappuworld.external.messenger.dto.DiscordEmbedField
import co.yappuworld.external.messenger.dto.DiscordEmbedMessage
import co.yappuworld.external.messenger.dto.MessageContent
import co.yappuworld.external.messenger.dto.TextMessage
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
    private val discordProperties: DiscordProperties
) : MessengerClient {

    override fun send(content: MessageContent) {
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
        val message = when (content) {
            is TextMessage -> DiscordMessage(content = content.text)
            is DiscordEmbedMessage -> DiscordMessage.of(content.toDiscordEmbed())
            else -> throw IllegalArgumentException(
                "지원하지 않는 메시지 타입입니다: ${content::class.simpleName}"
            )
        }

        RestTemplate().postForObject(
            discordProperties.webhook,
            HttpEntity(jacksonObjectMapper().writeValueAsString(message), headers),
            Unit::class.java
        )
    }

    private fun DiscordEmbedMessage.toDiscordEmbed() =
        DiscordEmbed.info(
            title = this.title,
            description = this.description,
            url = this.url,
            fields = this.fields.map {
                DiscordEmbedField(it.name, it.value, it.inline)
            }
        )
}
