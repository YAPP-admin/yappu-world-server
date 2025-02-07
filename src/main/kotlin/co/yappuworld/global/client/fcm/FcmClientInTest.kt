package co.yappuworld.global.client.fcm

import com.google.firebase.messaging.Notification
import org.springframework.stereotype.Component

@Component
class FcmClientInTest : FcmClient {

    override fun sendNotification(
        token: String,
        notification: Notification
    ) {
        TODO("Not yet implemented")
    }

    override fun sendNotification(
        tokens: List<String>,
        notification: Notification
    ) {
        TODO("Not yet implemented")
    }
}
