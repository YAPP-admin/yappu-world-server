package co.yappuworld.global.util

import org.springframework.security.crypto.bcrypt.BCrypt

object EncrytUtils {

    fun encrypt(plain: String): String {
        return BCrypt.hashpw(plain, BCrypt.gensalt())
    }

    fun isMatch(
        plain: String,
        hashed: String
    ): Boolean {
        return BCrypt.checkpw(plain, hashed)
    }
}
