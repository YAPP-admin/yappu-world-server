package co.yappuworld.user.domain.model

import co.yappuworld.global.persistence.BaseEntity
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("user_devices")
class UserDevice(
    val userId: UUID,
    fcmToken: String?
) : BaseEntity() {

    var fcmToken: String? = fcmToken
        private set

    fun withId(id: UUID): UserDevice = UserDevice(this.userId, this.fcmToken).apply { this.id = id }

    fun updateFcmToken(fcmToken: String) {
        this.fcmToken = fcmToken
    }
}
