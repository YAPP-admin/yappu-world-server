package co.yappuworld.user.domain.model

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.util.EncryptUtils
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
class User private constructor(
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    isActive: Boolean = true,
    @Id
    @JvmField
    val id: UUID
) : BaseEntity(), Persistable<UUID> {

    var isActive: Boolean = isActive
        private set

    constructor(
        email: String,
        password: String,
        name: String,
        role: UserRole
    ) : this(email, password, name, role, true, UlidCreator.getMonotonicUlid().toUuid())

    fun withId(id: UUID): User {
        return User(this.email, this.password, this.name, this.role, this.isActive, id)
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }

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

    fun isWithdrawn(): Boolean {
        return !this.isActive
    }
}
