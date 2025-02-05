package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.user.application.dto.response.MasterAlarmToggleAppResponse
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.UserAlarmSettingRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

private val logger = KotlinLogging.logger { }

@Service
class UserAlarmService(
    private val userAlarmSettingRepository: UserAlarmSettingRepository
) {

    @Transactional
    fun toggleMasterAlarm(userId: UUID): MasterAlarmToggleAppResponse {
        val userAlarm = userAlarmSettingRepository.findUserAlarmSettingOrNullByUserId(userId)
            ?: run {
                logger.error { "${userId}의 알람 데이터가 존재하지 않습니다. 데이터를 확인하세요" }
                throw BusinessException(UserError.USER_RELATED_DATA_NOT_FOUND)
            }
        userAlarm.toggleMaster()
        userAlarmSettingRepository.save(userAlarm)

        return MasterAlarmToggleAppResponse(userAlarm.master)
    }
}
