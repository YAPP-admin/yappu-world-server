package co.yappuworld.user.infrastructure

import java.util.UUID

interface UserSystemNotifier {

    fun notifySignUpRequestReceived(
        applicationId: UUID,
        applicantName: String
    )
}
