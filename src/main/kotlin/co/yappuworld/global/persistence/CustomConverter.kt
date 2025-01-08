package co.yappuworld.global.persistence

import co.yappuworld.user.domain.SignUpApplicantDetails
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
class UserApplicationWritingConverter : Converter<SignUpApplicantDetails, String> {
    override fun convert(source: SignUpApplicantDetails): String {
        return ObjectMapper().writeValueAsString(source)
    }
}

@ReadingConverter
class UserApplicationReadingConverter : Converter<String, SignUpApplicantDetails> {
    override fun convert(source: String): SignUpApplicantDetails {
        val om = jacksonObjectMapper()
        val result = om.readValue(source, SignUpApplicantDetails::class.java)
        return result
    }
}
