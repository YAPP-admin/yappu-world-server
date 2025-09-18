package co.yappuworld.external.map

import co.yappuworld.external.map.dto.AddressCoordinate
import co.yappuworld.external.map.dto.KakaoGeocodingResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class KakaoMapClient(
    private val kakaoProperties: KakaoProperties,
    private val restTemplate: RestTemplate
) : MapClient {

    override fun convertAddressToCoordinates(address: String): AddressCoordinate {
        val noWhiteSpaceAddress = address.replace(" ", "_")
        val url = "${kakaoProperties.mapApiHost}/v2/local/search/address.json?query=$noWhiteSpaceAddress"

        val headers = HttpHeaders().apply {
            set("Authorization", "KakaoAK ${kakaoProperties.restApiKey}")
        }

        val entity = HttpEntity<String>(headers)
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            KakaoGeocodingResponse::class.java
        )

        val document = response.body?.documents?.firstOrNull()
            ?: throw IllegalArgumentException("주소를 찾을 수 없습니다: $address")

        return AddressCoordinate(
            addressName = document.addressName,
            latitude = document.latitude.toDouble(),
            longitude = document.longitude.toDouble()
        )
    }
}
