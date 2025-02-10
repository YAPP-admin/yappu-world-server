package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_devices")
class UserDevice private constructor(
    val userId: UUID,
    fcmToken: String?,
    @Id
    @JvmField
    val id: UUID
) : BaseEntity(), Persistable<UUID> {

    var fcmToken: String? = fcmToken
        private set

    constructor(userId: UUID, fcmToken: String) : this(userId, fcmToken, UlidCreator.getMonotonicUlid().toUuid())

    fun withId(id: UUID): UserDevice {
        return UserDevice(this.userId, this.fcmToken, id)
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }

    fun updateFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }
}
