package co.yappuworld.external.fcm

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.firebase.messaging.AndroidConfig
import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
import com.google.firebase.messaging.ApsAlert
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

@Profile("local", "dev")
@Tag(name = "FCM 테스트용", description = "_")
@RestController
class FcmTestController(
    private val firebaseMessaging: FirebaseMessaging
) {

    data class FcmOnlyDataRequest(
        val token: String,
        val title: String,
        val body: String,
        val deeplink: String
    ) {

        @JsonIgnore
        fun getDataWithNotification(): Map<String, String> =
            mapOf(
                "title" to title,
                "body" to body,
                "deeplink" to deeplink
            )
    }

    @Operation(summary = "FCM")
    @PostMapping("/fcm")
    fun sendFcmWithContentAvailable(
        @RequestBody request: FcmOnlyDataRequest
    ) {
        try {
            firebaseMessaging.send(
                Message
                    .builder()
                    .setToken(request.token)
                    .setAndroidConfig(
                        AndroidConfig
                            .builder()
                            .putAllData(request.getDataWithNotification())
                            .build()
                    ).setApnsConfig(
                        ApnsConfig
                            .builder()
                            .setAps(
                                Aps
                                    .builder()
                                    .setContentAvailable(true)
                                    .setAlert(
                                        ApsAlert
                                            .builder()
                                            .setTitle(request.title)
                                            .setBody(request.body)
                                            .build()
                                    ).putCustomData("deeplink", request.deeplink)
                                    .build()
                            ).build()
                    ).build()
            )
        } catch (e: FirebaseMessagingException) {
            logger.warn {
                """
                    푸시 알림 전송에 실패했습니다.
                    실패 사유: ${e.messagingErrorCode.name}
                    fcm_token: ${request.token}
                """.trimIndent()
            }
        }
    }
}
