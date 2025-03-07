package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_alarm_settings")
class UserAlarmSetting private constructor(
    val userId: UUID,
    device: Boolean,
    master: Boolean
) : BaseEntity() {

    var device: Boolean = device
        private set
    var master: Boolean = master
        private set

    constructor(userId: UUID, deviceToggle: Boolean) : this(
        userId = userId,
        device = deviceToggle,
        master = true
    )

    fun withId(id: UUID): UserAlarmSetting =
        UserAlarmSetting(this.userId, this.device, this.master).apply { this.id = id }

    fun toggleMaster() {
        this.master = !this.master
    }

    fun updateDeviceAlarm(deviceToggle: Boolean) {
        this.device = deviceToggle
    }
}
