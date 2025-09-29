package co.yappuworld.user.infrastructure

import co.yappuworld.external.messenger.MessengerClient
import co.yappuworld.external.messenger.dto.DiscordEmbed
import co.yappuworld.external.messenger.dto.DiscordEmbedField
import co.yappuworld.global.property.AdminProperties
import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class DiscordUserSystemNotifier(
    private val messengerClient: MessengerClient,
    private val adminProperties: AdminProperties
) : UserSystemNotifier {

    override fun notifySignUpRequestReceived(signUpApplication: SignUpApplicationEntity) {
        messengerClient.send(
            DiscordEmbed.info(
                title = "💡 회원가입 신청을 확인해주세요 💡",
                url = "${adminProperties.domain}/admin/members/application",
                fields = listOf(
                    DiscordEmbedField(
                        name = "회원가입 ID",
                        value = signUpApplication.id.toString()
                    ),
                    DiscordEmbedField(
                        name = "유저 이름",
                        value = signUpApplication.getApplicantName()
                    )
                )
            )
        )
    }
}
