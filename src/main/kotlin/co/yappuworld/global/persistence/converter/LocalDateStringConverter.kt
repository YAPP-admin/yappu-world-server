package co.yappuworld.global.persistence.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Converter(autoApply = true)
class LocalDateStringConverter : AttributeConverter<LocalDate, String> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE // yyyy-MM-dd

    override fun convertToDatabaseColumn(attribute: LocalDate?): String? = attribute?.format(formatter)

    override fun convertToEntityAttribute(dbData: String?): LocalDate? = dbData?.let { LocalDate.parse(it, formatter) }
}
