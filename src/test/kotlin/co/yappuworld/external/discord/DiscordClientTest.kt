package co.yappuworld.external.discord

import io.github.oshai.kotlinlogging.KotlinLogging
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

private val logger = KotlinLogging.logger { }

@SpringBootTest
class DiscordClientTest {

    @Autowired
    lateinit var discordClient: DiscordClient

    @Test
    fun test() {
        logger.error { "discord webhook: ${discordClient.discordProperty.webhook}" }
        discordClient.send("테스트 컨텐츠")
    }
}
