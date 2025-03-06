package co.yappuworld.external.fcm

import com.google.firebase.messaging.BatchResponse
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingException
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.MulticastMessage
import com.google.firebase.messaging.Notification
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger { }

@Component
class FcmClient(
    private val firebaseMessaging: FirebaseMessaging
) {

    fun sendNotification(
        token: String,
        notification: Notification
    ) {
        try {
            firebaseMessaging.send(
                Message
                    .builder()
                    .setToken(token)
                    .setNotification(notification)
                    .build()
            )
        } catch (e: FirebaseMessagingException) {
            logger.warn {
                """
                    푸시 알림 전송에 실패했습니다.
                    실패 사유: ${e.messagingErrorCode.name}
                    fcm_token: $token
                """.trimIndent()
            }
        }
    }

    fun sendNotification(
        tokens: List<String>,
        notification: Notification
    ) {
        if (tokens.size > 500) {
            val message = "한 번에 요청할 수 있는 푸시 알림의 수는 500개입니다. 요청 개수: ${tokens.size}"
            logger.warn { message }
            throw IllegalArgumentException(message)
        }

        try {
            val responses = firebaseMessaging.sendEachForMulticast(
                MulticastMessage
                    .builder()
                    .addAllTokens(tokens)
                    .setNotification(notification)
                    .build()
            )
            loggingFirebaseMessaging(responses)
        } catch (e: FirebaseMessagingException) {
            logger.warn {
                """
                    다수의 푸시 알림 전송에 실패했습니다.
                    실패 사유: ${e.messagingErrorCode.name}
                """.trimIndent()
            }
        }
    }

    private fun loggingFirebaseMessaging(responses: BatchResponse) {
        logger.info {
            """
                푸시 알림 전송 결과: 성공 ${responses.successCount}, 실패 ${responses.failureCount}
                에러코드별 실패 횟수
                ${getCountByErrorCode(responses)}
            """.trimIndent()
        }
    }

    private fun getCountByErrorCode(response: BatchResponse): Map<String, Int> =
        response.responses
            .filter { !it.isSuccessful }
            .groupingBy { it.exception.messagingErrorCode.name }
            .eachCount()
}
