package co.yappuworld.user.application

import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.SecurityUser
import co.yappuworld.global.security.Token
import co.yappuworld.global.vo.UserRole
import co.yappuworld.user.presentation.dto.request.UserSignUpRequestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserAuthService(
    private val jwtGenerator: JwtGenerator
) {

    @Transactional
    fun signUp(
        request: UserSignUpRequestDto,
        now: LocalDateTime
    ): Token {
        return jwtGenerator.generateToken(
            SecurityUser(
                UUID.randomUUID(),
                UserRole.ACTIVE
            ),
            now
        )
    }
}
