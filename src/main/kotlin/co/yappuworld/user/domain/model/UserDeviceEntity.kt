package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "user_devices")
class UserDeviceEntity(
    val userId: UUID,
    fcmToken: String?
) : BaseJpaEntity() {

    var fcmToken: String? = fcmToken
        private set

    fun updateFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }
}
