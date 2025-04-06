package co.yappuworld.external.fcm

import com.fasterxml.jackson.annotation.JsonIgnore
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
    private val fcmClient: FcmClient
) {

    data class SingleFcm(
        val token: String,
        val title: String,
        val body: String,
        val deeplink: String
    ) {

        @JsonIgnore
        fun toFcmMessage(): FcmMessage =
            FcmMessage(
                title = title,
                body = body,
                deeplink = deeplink
            )
    }

    data class MultiFcm(
        val tokens: List<String>,
        val title: String,
        val body: String,
        val deeplink: String
    ) {

        @JsonIgnore
        fun toFcmMessage(): FcmMessage =
            FcmMessage(
                title = title,
                body = body,
                deeplink = deeplink
            )
    }

    @Operation(summary = "FCM")
    @PostMapping("/fcm")
    fun sendFcm(
        @RequestBody request: SingleFcm
    ) {
        fcmClient.sendNotification(
            request.token,
            request.toFcmMessage()
        )
    }

    @Operation(summary = "FCM 여러개")
    @PostMapping("/fcm-multi")
    fun sendFcms(
        @RequestBody request: MultiFcm
    ) {
        fcmClient.sendNotification(
            request.tokens,
            request.toFcmMessage()
        )
    }
}
