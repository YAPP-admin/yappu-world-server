package co.yappuworld.user.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.support.environment.CustomDataJpaFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getUserDeviceEntityFixture
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.jpa.UserDeviceRepository
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserDeviceFindServiceTest @Autowired constructor(
    private val userDeviceRepository: UserDeviceRepository
) : CustomDataJpaFeatureSpec({

        val userDeviceFindService = UserDeviceFindService(userDeviceRepository)

        feature("유저 기기정보 조회") {

            scenario("존재하지 않으면 예외가 발생") {
                shouldThrowExactly<BusinessException> { userDeviceFindService.findUserDevice(UUID.randomUUID()) }
                    .error shouldBe UserError.USER_RELATED_DATA_NOT_FOUND
            }

            scenario("존재하면 기기정보를 반환") {
                val userDevice = userDeviceRepository.save(getUserDeviceEntityFixture())
                userDeviceFindService.findUserDevice(userDevice.userId).id shouldBe userDevice.id
            }
        }
    })
