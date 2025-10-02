package co.yappuworld.external.messenger

import co.yappuworld.external.messenger.dto.TextMessage
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DiscordClientTest @Autowired constructor(
    private val discordClient: DiscordClient
) {

    @Test
    fun test() {
        discordClient.send(TextMessage("테스트 컨텐츠"))
    }
}
