package co.yappuworld.global.persistence

import co.yappuworld.user.infrastructure.jpa.ApplicationDetails
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ApplicationDetailsConverter : AttributeConverter<ApplicationDetails, String> {
    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: ApplicationDetails?): String =
        attribute?.let {
            objectMapper.writeValueAsString(it)
        } ?: "{}"

    override fun convertToEntityAttribute(dbData: String?): ApplicationDetails? =
        dbData?.let {
            jacksonObjectMapper().readValue(it, ApplicationDetails::class.java)
        }
}
