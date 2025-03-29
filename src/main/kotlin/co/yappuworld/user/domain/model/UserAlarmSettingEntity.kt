package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseJpaEntity
import jakarta.persistence.Entity
import java.util.UUID

@Entity
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
