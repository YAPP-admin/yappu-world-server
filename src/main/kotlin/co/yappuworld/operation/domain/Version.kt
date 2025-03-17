package co.yappuworld.operation.domain

import co.yappuworld.global.exception.BusinessException
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import io.swagger.v3.oas.annotations.media.Schema

/**
 * @param value x.y.z
 */
class Version
    @JsonCreator
    constructor(
        @field:Schema(name = "version", description = "버전 정보 (x.y.z)", required = true)
        val value: String
    ) {

        @JsonIgnore
        val major: Int

        @JsonIgnore
        val minor: Int

        @JsonIgnore
        val patch: Int

        init {
            val splited = value.split(".")
            if (splited.size != 3) {
                throw BusinessException(ConfigError.WRONG_VERSION_FORMAT)
            }

            major = splited[0].toInt()
            minor = splited[1].toInt()
            patch = splited[2].toInt()
        }

        fun isBeforeThan(other: Version): Boolean =
            major < other.major ||
                (major == other.major && minor < other.minor) ||
                (major == other.major && minor == other.minor && patch < other.patch)

        override fun equals(other: Any?): Boolean {
            if (other !is Version) return false
            return major == other.major && minor == other.minor && patch == other.patch
        }

        override fun hashCode(): Int = javaClass.hashCode()

        @JsonValue
        override fun toString(): String = value
    }
