package co.yappuworld.external.map

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kakao")
data class KakaoProperties(
    @param:JsonProperty("map_api_host")
    val mapApiHost: String,
    @param:JsonProperty("rest_api_key")
    val restApiKey: String
)
