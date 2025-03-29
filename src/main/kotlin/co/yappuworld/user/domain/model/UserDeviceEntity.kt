package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseJpaEntity
import jakarta.persistence.Entity
import java.util.UUID

@Entity
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
