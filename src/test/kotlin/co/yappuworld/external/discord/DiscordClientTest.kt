package co.yappuworld.external.discord

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@EnabledIfSystemProperty(
    named = "spring.profiles.active",
    matches = "test"
)
class DiscordClientTest {

    @Autowired
    lateinit var discordClient: DiscordClient

    @Test
    fun test() {
        discordClient.send("테스트 컨텐츠")
    }
}
