package co.yappuworld.external.fcm

import com.google.firebase.messaging.ApnsConfig
import com.google.firebase.messaging.Aps
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
import java.util.UUID

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
        val type: String,
        val id: UUID
    ) {

        fun toData(): Map<String, String> =
            mapOf(
                "title" to title,
                "body" to body,
                "type" to type,
                "id" to id.toString()
            )
    }

    @Operation(summary = "FCM Data 필드만 있는 메세지")
    @PostMapping("/fcm-only-data")
    fun sendNotificationOnlyData(
        @RequestBody request: FcmOnlyDataRequest
    ) {
        try {
            firebaseMessaging.send(
                Message
                    .builder()
                    .setToken(request.token)
                    .putAllData(request.toData())
                    .build()
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

    @Operation(summary = "Content Available 설정된 FCM 메세지")
    @PostMapping("/fcm-content-available")
    fun sendFcmWithContentAvailable(
        @RequestBody request: FcmOnlyDataRequest
    ) {
        try {
            firebaseMessaging.send(
                Message
                    .builder()
                    .setApnsConfig(
                        ApnsConfig
                            .builder()
                            .setAps(
                                Aps
                                    .builder()
                                    .setContentAvailable(true)
                                    .build()
                            ).build()
                    ).putAllData(request.toData())
                    .build()
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
