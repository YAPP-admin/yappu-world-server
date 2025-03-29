package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseJpaEntity
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import jakarta.persistence.Entity

@Entity
class UserEntity(
    email: String,
    password: String,
    name: String,
    role: UserRole
) : BaseJpaEntity() {

    var email: String = email
        private set
    var password: String = password
        private set
    var name: String = name
        private set
    var role: UserRole = role
        private set
    var isActive: Boolean = true
        private set

    fun checkPassword(plainPassword: String) {
        if (!EncryptUtils.isMatch(plainPassword, this.password)) {
            throw BusinessException(UserError.WRONG_LOGIN_USER_INFORMATION)
        }
    }

    fun withdraw() {
        if (!this.isActive) {
            throw BusinessException(UserError.ALREADY_WITHDRAWN_USER)
        }

        this.isActive = false
    }

    fun isWithdrawn(): Boolean = !this.isActive

    fun updateRole(role: UserRole) {
        this.role = role
    }

    fun updateDetails(
        name: String,
        email: String
    ) {
        this.name = name
        this.email = email
    }
}
