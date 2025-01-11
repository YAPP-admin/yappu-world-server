package co.yappuworld.global.persistence

import co.yappuworld.user.domain.UserSignUpApplication
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import java.util.UUID

@WritingConverter
class UuidIdentifierWritingConverter : Converter<UUID, String> {
    override fun convert(source: UUID): String {
        return source.toString()
    }
}

@ReadingConverter
class UuidIdentifierReadingConverter : Converter<String, UUID> {
    override fun convert(source: String): UUID {
        return UUID.fromString(source)
    }
}

@WritingConverter
class UserApplicationWritingConverter : Converter<UserSignUpApplication, String> {
    override fun convert(source: UserSignUpApplication): String {
        return ObjectMapper().writeValueAsString(source)
    }
}

@ReadingConverter
class UserApplicationReadingConverter : Converter<String, UserSignUpApplication> {
    override fun convert(source: String): UserSignUpApplication {
        val om = jacksonObjectMapper()
        val result = om.readValue(source, UserSignUpApplication::class.java)
        return result
    }
}
