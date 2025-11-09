package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.jpa.ActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import co.yappuworld.user.infrastructure.jpa.UserAlarmSettingRepository
import co.yappuworld.user.infrastructure.jpa.UserDeviceRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class SignUpExecutorConcurrencyTest @Autowired constructor(
    private val signUpExecutor: SignUpExecutor,
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: SignUpApplicationRepository,
    private val activityUnitRepository: ActivityUnitRepository,
    private val userDeviceRepository: UserDeviceRepository,
    private val userAlarmSettingRepository: UserAlarmSettingRepository
) : SpringBootTestFeatureSpec({

        afterTest {
            userRepository
                .findUserOrNullByEmail("concurrency-test")
                ?.let { user ->
                    val activityUnits = activityUnitRepository.findAllByUserId(user.id)
                    activityUnitRepository.deleteAll(activityUnits)

                    userDeviceRepository
                        .findUserDeviceOrNullByUserId(user.id)
                        ?.let { userDeviceRepository.delete(it) }

                    userAlarmSettingRepository
                        .findUserAlarmSettingOrNullByUserId(user.id)
                        ?.let { userAlarmSettingRepository.delete(it) }

                    userRepository.delete(user)
                }

            userRepository
                .findUserOrNullByEmail("abcde@gmail.com")
                ?.let { user ->
                    val activityUnits = activityUnitRepository.findAllByUserId(user.id)
                    activityUnitRepository.deleteAll(activityUnits)

                    userDeviceRepository
                        .findUserDeviceOrNullByUserId(user.id)
                        ?.let { userDeviceRepository.delete(it) }

                    userAlarmSettingRepository
                        .findUserAlarmSettingOrNullByUserId(user.id)
                        ?.let { userAlarmSettingRepository.delete(it) }

                    userRepository.delete(user)
                }

            val testEmails = listOf("concurrency-test", "abcde@gmail.com")
            val applications = signUpApplicationRepository.findAllByApplicantEmailIn(testEmails)
            signUpApplicationRepository.deleteAll(applications)
        }

        feature("동시에 같은 이메일로") {

            scenario("회원가입") {
                val threadCount = 20
                val executorService = Executors.newFixedThreadPool(32)
                val latch = CountDownLatch(threadCount)

                repeat(threadCount) {
                    val application = getSignUpApplicationEntityFixture(
                        email = "concurrency-test"
                    )
                    executorService.submit {
                        try {
                            signUpExecutor.signUp(
                                application = application,
                                role = UserRole.ACTIVE,
                                now = getCurrentDateTimeInKST()
                            )
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                userRepository.count() shouldBe 1
            }

            scenario("가입 신청") {
                val threadCount = 20
                val executorService = Executors.newFixedThreadPool(32)
                val latch = CountDownLatch(threadCount)

                repeat(threadCount) {
                    val application = getSignUpApplicationEntityFixture(
                        email = "abcde@gmail.com"
                    )
                    executorService.submit {
                        try {
                            signUpExecutor.submit(application = application)
                        } finally {
                            latch.countDown()
                        }
                    }
                }

                latch.await()
                signUpApplicationRepository.count() shouldBe 1
            }
        }
    })
