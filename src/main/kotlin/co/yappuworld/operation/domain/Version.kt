package co.yappuworld.operation.domain

import co.yappuworld.global.exception.BusinessException
import com.fasterxml.jackson.annotation.JsonIgnore
import io.swagger.v3.oas.annotations.media.Schema
import org.springdoc.core.annotations.ParameterObject

/**
 * @param version x.y.z
 */
@ParameterObject
class Version(
    @field:Schema(description = "버전 정보 (x.y.z)", required = true)
    val version: String
) {

    @JsonIgnore
    val major: Int

    @JsonIgnore
    val minor: Int

    @JsonIgnore
    val patch: Int

    init {
        val splited = version.split(".")
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

    override fun toString(): String = version
}
