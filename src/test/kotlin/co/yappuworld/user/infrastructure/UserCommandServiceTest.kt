package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.entity.UserEntity
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.jpa.UserDeviceRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class UserCommandServiceTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val userAlarmSettingRepository: UserAlarmSettingRepository,
    private val userDeviceRepository: UserDeviceRepository
) : CustomDataJpaTestFeatureSpec({

        val userCommandService = UserCommandService(
            userRepository = userRepository,
            activityUnitRepository = activityUnitRepository,
            userAlarmSettingRepository = userAlarmSettingRepository,
            userDeviceRepository = userDeviceRepository
        )

        feature("회원가입") {

            scenario("지정한 역할대로 저장된다.") {
                UserRole.entries.forEach { role ->
                    val email = "abc${role.label}@abc.com"
                    userCommandService.signUp(getSignUpApplicationEntityFixture(email = email), role)
                    userRepository.findUserOrNullByEmail(email)?.role shouldBe role
                }
            }

            scenario("저장하면 관련된 유저 데이터가 생성된다.") {
                val user = userCommandService.signUp(getSignUpApplicationEntityFixture(), UserRole.ACTIVE)
                userRepository.findByIdOrNull(user.id).shouldNotBeNull()
                activityUnitRepository.findAllByUserId(user.id).shouldNotBeEmpty()
                userAlarmSettingRepository.findUserAlarmSettingOrNullByUserId(user.id).shouldNotBeNull()
                userDeviceRepository.findUserDeviceOrNullByUserId(user.id).shouldNotBeNull()
            }
        }

        feature("회원 탈퇴") {
            fun signUp(application: SignUpApplicationEntity): UserEntity =
                userCommandService.signUp(application, UserRole.ACTIVE)

            scenario("회원 탈퇴 시 isActive가 False로 변경된다.") {
                val user = signUp(getSignUpApplicationEntityFixture())
                userCommandService.deactivate(user)

                user.isActive shouldBe false
            }

            scenario("회원 탈퇴 시 기기와 알림 정보는 삭제된다.") {
                val user = signUp(getSignUpApplicationEntityFixture())
                userCommandService.deactivate(user)

                userDeviceRepository.findUserDeviceOrNullByUserId(user.id).shouldBeNull()
                userAlarmSettingRepository.findUserAlarmSettingOrNullByUserId(user.id).shouldBeNull()
            }
        }
    })
