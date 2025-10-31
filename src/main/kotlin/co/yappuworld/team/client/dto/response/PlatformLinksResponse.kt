package co.yappuworld.team.client.dto.response

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "플랫폼 링크")
data class PlatformLinksResponse(
    @Schema(description = "앱스토어 링크", example = "https://apps.apple.com")
    val app_store: String? = null,
    @Schema(description = "구글 플레이 링크", example = "https://play.google.com/store/apps")
    val google_play: String? = null,
    @Schema(description = "웹 링크", example = "https://yappworld.com")
    val web: String? = null
) {
    companion object {
        fun from(serviceLinks: ServiceLinks?): PlatformLinksResponse? {
            if (serviceLinks == null) return null
            if (serviceLinks.appStore == null && serviceLinks.googleStore == null && serviceLinks.web == null) {
                return null
            }
            return PlatformLinksResponse(
                app_store = serviceLinks.appStore,
                google_play = serviceLinks.googleStore,
                web = serviceLinks.web
            )
        }
    }
}
