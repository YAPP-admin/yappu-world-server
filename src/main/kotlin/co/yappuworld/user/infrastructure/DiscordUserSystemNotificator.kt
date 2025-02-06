package co.yappuworld.user.infrastructure

import co.yappuworld.external.discord.DiscordClient
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class DiscordUserSystemNotificator(
    private val discordClient: DiscordClient
) : UserSystemNotificator {

    override fun notify(content: String) {
        discordClient.send(content)
    }
}
