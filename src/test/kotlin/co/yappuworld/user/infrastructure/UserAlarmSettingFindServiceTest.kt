package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.support.environment.CustomDataJpaFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getUserAlarmSettingEntityFixture
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.UserAlarmSettingRepository
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserAlarmSettingFindServiceTest @Autowired constructor(
    private val userAlarmSettingRepository: UserAlarmSettingRepository
) : CustomDataJpaFeatureSpec({

        val userAlarmSettingFindService = UserAlarmSettingFindService(userAlarmSettingRepository)

        feature("유저의 알람 설정 정보 조회") {

            scenario("유저의 알람 설정 정보가 존재하지 않으면 예외 발생") {
                shouldThrowExactly<BusinessException> {
                    userAlarmSettingFindService.findUserAlarmSetting(UUID.randomUUID())
                }.error shouldBe UserError.USER_RELATED_DATA_NOT_FOUND
            }

            scenario("존재하면 조회 성공") {
                val userId = UUID.randomUUID()
                val userAlarmSetting = getUserAlarmSettingEntityFixture(userId = userId)
                userAlarmSettingRepository.saveAndFlush(userAlarmSetting)

                shouldNotThrowAny { userAlarmSettingFindService.findUserAlarmSetting(userId) }
            }
        }
    })
