package co.yappuworld.user.domain.entity

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.global.util.StringUtils.isPhoneNumber
import co.yappuworld.user.domain.vo.Gender
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    email: String,
    password: String,
    name: String,
    role: UserRole
) : BaseEntity() {

    var email: String = email
        private set
    var password: String = password
        private set
    var name: String = name
        private set

    @Enumerated(EnumType.STRING)
    var role: UserRole = role
        private set
    var isActive: Boolean = true
        private set

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null
        private set

    var phoneNumber: String? = null

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
        email: String,
        gender: String?,
        phoneNumber: String?
    ) {
        this.name = name
        this.email = email
        gender?.let { this.gender = Gender.fromLabel(it) }
        phoneNumber.takeIf { it != null && it.isPhoneNumber() }.let { this.phoneNumber = it }
    }

    fun checkAdminAccessibility() {
        if (!this.role.canAccessAdminPage()) {
            throw BusinessException(UserError.NO_AUTH_FOR_ADMIN_PAGE)
        }
    }
}
