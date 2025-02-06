package co.yappuworld.external.fcm

import com.google.firebase.messaging.Notification

interface FcmClient {

    fun sendNotification(
        token: String,
        notification: Notification
    )

    fun sendNotification(
        tokens: List<String>,
        notification: Notification
    )
}
