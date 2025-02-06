package co.yappuworld.user.infrastructure

import co.yappuworld.external.discord.DiscordClient
import co.yappuworld.external.discord.dto.DiscordEmbed
import co.yappuworld.external.discord.dto.DiscordEmbedField
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Primary
class DiscordUserSystemNotifier(
    private val discordClient: DiscordClient
) : UserSystemNotifier {

    override fun notifySignUpRequestReceived(
        applicationId: UUID,
        applicantName: String
    ) {
        discordClient.send(
            DiscordEmbed.info(
                title = "💡 회원가입 신청을 확인해주세요 💡",
                // TODO: 추후 어드민 링크로 대체
                url = "https://www.naver.com",
                fields = listOf(
                    DiscordEmbedField(
                        name = "회원가입 ID",
                        value = applicationId.toString()
                    ),
                    DiscordEmbedField(
                        name = "유저 이름",
                        value = applicantName
                    )
                )
            )
        )
    }
}
