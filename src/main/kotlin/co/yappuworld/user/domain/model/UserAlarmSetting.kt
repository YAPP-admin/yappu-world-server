package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_alarm_settings")
class UserAlarmSetting private constructor(
    val userId: UUID,
    val master: Boolean,
    @Id
    @JvmField
    val id: UUID = UlidCreator.getMonotonicUlid().toUuid()
) : BaseEntity(), Persistable<UUID> {

    constructor(userId: UUID, masterToggle: Boolean) : this(
        userId,
        masterToggle,
        UlidCreator.getMonotonicUlid().toUuid()
    )

    fun withId(id: UUID): UserAlarmSetting {
        return UserAlarmSetting(this.userId, this.master, id)
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !this.isCreatedAtInitialized()
    }
}
