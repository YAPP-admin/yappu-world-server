package co.yappuworld.external.discord.dto

data class DiscordMessage(
    val content: String? = null,
    val embeds: List<DiscordEmbed>? = null
) {
    companion object {
        fun of(embed: DiscordEmbed): DiscordMessage = DiscordMessage(embeds = listOf(embed))
    }
}
