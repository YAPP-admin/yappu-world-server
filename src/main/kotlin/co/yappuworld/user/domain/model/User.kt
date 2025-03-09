package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
class User private constructor(
    email: String,
    password: String,
    name: String,
    role: UserRole,
    isActive: Boolean = true
) : BaseEntity() {

    var isActive: Boolean = isActive
        private set

    var email: String = email
        private set
    var password: String = password
        private set
    var name: String = name
        private set
    var role: UserRole = role
        private set

    constructor(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ) : this(email, password, name, role, true)

    fun withId(id: UUID): User =
        User(this.email, this.password, this.name, this.role, this.isActive).apply { this.id = id }

    override fun getId(): UUID = this.id

    override fun isNew(): Boolean = !isCreatedAtInitialized()

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
