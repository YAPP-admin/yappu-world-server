package co.yappuworld.global.persistence

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter
import java.util.UUID

@WritingConverter
class UuidToStringConverter : Converter<UUID, String> {

    override fun convert(source: UUID): String? {
        return source.toString()
    }
}
