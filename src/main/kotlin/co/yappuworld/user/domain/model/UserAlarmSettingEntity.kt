package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_alarm_settings")
class UserAlarmSettingEntity(
    val userId: UUID,
    device: Boolean
) : BaseJpaEntity() {

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
