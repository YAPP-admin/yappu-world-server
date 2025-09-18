package co.yappuworld.external.map.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoGeocodingResponse(
    val documents: List<Document>
) {
    data class Document(
        @param:JsonProperty("x")
        val longitude: String,
        @param:JsonProperty("y")
        val latitude: String,
        @param:JsonProperty("address_name")
        val addressName: String
    )
}
