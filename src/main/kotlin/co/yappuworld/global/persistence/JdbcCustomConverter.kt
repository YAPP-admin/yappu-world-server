package co.yappuworld.global.persistence

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.util.UUID

@WritingConverter
class UuidIdentifierWritingConverter : Converter<UUID, String> {
    override fun convert(source: UUID): String = source.toString()
}

@ReadingConverter
class UuidIdentifierReadingConverter : Converter<String, UUID> {
    override fun convert(source: String): UUID = UUID.fromString(source)
}
