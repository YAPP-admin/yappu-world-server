package co.yappuworld.global.persistence

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import java.util.UUID

@ReadingConverter
class StringToUuidConverter : Converter<UUID, String> {
    override fun convert(source: UUID): String {
        return source.toString()
    }
}
