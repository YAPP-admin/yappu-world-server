package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.domain.entity.UserDeviceEntity
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.UserDeviceRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserDeviceFindService(
    private val userDeviceRepository: UserDeviceRepository
) {

    fun findUserDevice(userId: UUID): UserDeviceEntity =
        userDeviceRepository.findUserDeviceOrNullByUserId(userId)
            ?: throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
}
