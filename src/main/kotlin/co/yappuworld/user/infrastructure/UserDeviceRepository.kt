package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.UserDevice
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface UserDeviceRepository : CrudRepository<UserDevice, UUID> {

    fun findUserDeviceOrNullByUserId(userId: UUID): UserDevice?
}
