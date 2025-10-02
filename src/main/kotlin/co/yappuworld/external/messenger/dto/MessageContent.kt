package co.yappuworld.external.messenger.dto

interface MessageContent

data class EmbedMessage(
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
