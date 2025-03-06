package co.yappuworld.global.util

import org.springframework.security.crypto.bcrypt.BCrypt

object EncryptUtils {

    fun encrypt(plain: String): String = BCrypt.hashpw(plain, BCrypt.gensalt())

    fun isMatch(
        plain: String,
        hashed: String
    ): Boolean = BCrypt.checkpw(plain, hashed)
}
