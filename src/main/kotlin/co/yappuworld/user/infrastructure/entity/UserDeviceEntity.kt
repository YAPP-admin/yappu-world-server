package co.yappuworld.user.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_devices")
class UserDeviceEntity(
    val userId: UUID,
    fcmToken: String?
) : BaseEntity() {

    var fcmToken: String? = fcmToken
        private set

    fun updateFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }
}
