package co.yappuworld.external.messenger

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DiscordClientTest @Autowired constructor(
    private val messengerClient: MessengerClient
) {

    @Test
    fun test() {
        messengerClient.send("테스트 컨텐츠")
    }
}
