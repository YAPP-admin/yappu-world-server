package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

@SpringBootTest
class SignUpFacadeConcurrencyTest @Autowired constructor(
    private val signUpExecutor: SignUpExecutor,
    private val userRepository: UserRepository,
    private val signUpApplicationRepository: SignUpApplicationRepository
) : FeatureSpec({

        afterTest {
            userRepository.deleteAll()
            signUpApplicationRepository.deleteAll()
        }

        feature("동시에 같은 이메일로") {

            scenario("회원가입") {
                val threadCount = 20
                val executorService = Executors.newFixedThreadPool(32)
                val latch = CountDownLatch(threadCount)

                repeat(threadCount) {
                    executorService.submit {
                        try {
                            signUpExecutor.signUp(
                                application = getSignUpApplicationEntityFixture(
                                    email = "abc@gmail.com"
                                ),
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
                    executorService.submit {
                        try {
                            signUpExecutor.submit(
                                application = getSignUpApplicationEntityFixture(
                                    email = "abc@gmail.com"
                                )
                            )
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
