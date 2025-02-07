package co.yappuworld.external.discord

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.IfProfileValue

@SpringBootTest
@IfProfileValue(name = "spring.profiles.active", value = "local")
class DiscordClientTest {

    @Autowired
    lateinit var discordClient: DiscordClient

    @Test
    fun test() {
        discordClient.send("테스트 컨텐츠")
    }
}
