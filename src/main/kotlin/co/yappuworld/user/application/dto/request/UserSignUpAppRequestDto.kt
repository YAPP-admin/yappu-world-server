package co.yappuworld.user.application.dto.request

import co.yappuworld.global.vo.UserRole
import co.yappuworld.user.domain.User
import com.github.f4b6a3.ulid.UlidCreator

data class UserSignUpAppRequestDto(
    val email: String,
    val password: String,
    val name: String,
    val activityUnits: List<ActivityUnitAppRequestDto>,
    val signUpCode: String
) {
    fun toUser(role: UserRole): User {
        return User(
            UlidCreator.getMonotonicUlid().toUuid(),
            this.email,
            this.password,
            this.name,
            role
        )
    }
}
