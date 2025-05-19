package co.yappuworld.user.infrastructure.jpa

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserDeviceRepository : JpaRepository<UserDeviceEntity, UUID> {

    fun findUserDeviceOrNullByUserId(userId: UUID): UserDeviceEntity?
}
