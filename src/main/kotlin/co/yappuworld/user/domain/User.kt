package co.yappuworld.user.domain

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.global.vo.UserRole
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("users")
class User(
    @Id
    @JvmField
    val id: UUID,
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val isActive: Boolean = true
) : BaseEntity(), Persistable<UUID> {

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }
}
