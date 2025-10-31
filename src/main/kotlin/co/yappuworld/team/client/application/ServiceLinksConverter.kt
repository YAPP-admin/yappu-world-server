package co.yappuworld.team.client.application

import co.yappuworld.team.infrastructure.entity.ServiceLinks
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class ServiceLinksConverter : AttributeConverter<ServiceLinks?, String?> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: ServiceLinks?): String =
        attribute?.let {
            objectMapper.writeValueAsString(it)
        } ?: "{}"

    override fun convertToEntityAttribute(dbData: String?): ServiceLinks? =
        dbData?.let {
            jacksonObjectMapper().readValue(it, ServiceLinks::class.java)
        }
}
