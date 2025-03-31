package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.UserDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserDeviceJpaRepository : JpaRepository<UserDeviceEntity, UUID> {
    fun findUserDeviceOrNullByUserId(userId: UUID): UserDeviceEntity?
}
