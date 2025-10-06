package co.yappuworld.external.messenger.dto

interface MessageContent

data class TextMessage(
    val text: String
) : MessageContent

data class DiscordEmbedMessage(
    val title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val fields: List<EmbedField> = emptyList()
) : MessageContent

data class EmbedField(
    val name: String,
    val value: String,
    val inline: Boolean = false
)
