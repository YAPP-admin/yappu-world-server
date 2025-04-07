package co.yappuworld.global.util

object StringUtils {

    fun String.isPhoneNumber(): Boolean {
        val regex = Regex("^010-\\d{4}-\\d{4}$")
        return this.matches(regex)
    }
}
