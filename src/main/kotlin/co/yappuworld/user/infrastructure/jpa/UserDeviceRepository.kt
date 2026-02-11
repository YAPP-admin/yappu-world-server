package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.user.infrastructure.entity.UserDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserDeviceRepository : JpaRepository<UserDeviceEntity, UUID> {

    fun findUserDeviceOrNullByUserId(userId: UUID): UserDeviceEntity?

    fun deleteByUserId(userId: UUID)
}
