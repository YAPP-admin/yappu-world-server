package co.yappuworld.global.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Converter(autoApply = true)
class LocalTimeStringConverter : AttributeConverter<LocalTime, String> {

    private val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    override fun convertToDatabaseColumn(attribute: LocalTime?): String? = attribute?.format(formatter)

    override fun convertToEntityAttribute(dbData: String?): LocalTime? = dbData?.let { LocalTime.parse(it, formatter) }
}
