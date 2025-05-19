package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_alarm_settings")
class UserAlarmSettingEntity(
    val userId: UUID,
    device: Boolean
) : BaseEntity() {

    var device: Boolean = device
        private set
    var master: Boolean = true
        private set

    fun toggleMaster() {
        this.master = !this.master
    }

    fun updateDeviceAlarm(deviceToggle: Boolean) {
        this.device = deviceToggle
    }
}
