package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.user.domain.UserRole
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
    val isActive: Boolean = true,
    @Id
    @JvmField
    val id: UUID
) : BaseEntity(), Persistable<UUID> {

    constructor(
        email: String,
        password: String,
        name: String,
        role: UserRole,
        isActive: Boolean = true
    ) : this(email, password, name, role, isActive, UlidCreator.getMonotonicUlid().toUuid())

    fun withId(id: UUID): User {
        return User(this.email, this.password, this.name, this.role, this.isActive, id)
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }
}
