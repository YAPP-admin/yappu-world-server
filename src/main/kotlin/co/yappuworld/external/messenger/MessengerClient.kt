package co.yappuworld.external.messenger

import co.yappuworld.external.messenger.dto.DiscordEmbed

interface MessengerClient {
    fun send(content: String)
    fun send(embed: DiscordEmbed)
}
