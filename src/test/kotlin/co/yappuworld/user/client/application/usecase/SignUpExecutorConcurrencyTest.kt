package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.UserRole
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class SignUpExecutorConcurrencyTest @Autowired constructor(
    private val signUpExecutor: SignUpExecutor,
    private val jdbcTemplate: JdbcTemplate
) : SpringBootTestFeatureSpec({

        fun countUsersByEmail(email: String): Int =
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM USERS WHERE EMAIL = ?",
                Int::class.java,
                email
            ) ?: 0

        fun countSignUpApplicationsByEmail(email: String): Int =
            jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SIGN_UP_APPLICATION WHERE APPLICANT_EMAIL = ?",
                Int::class.java,
                email
            ) ?: 0

        fun deleteUsersByEmails(vararg emails: String) {
            val placeholders = emails.joinToString(",") { "?" }
            jdbcTemplate.update(
                "DELETE FROM ACTIVITY_UNITS WHERE USER_ID IN (SELECT ID FROM USERS WHERE EMAIL IN ($placeholders))",
                *emails
            )
            jdbcTemplate.update(
                "DELETE FROM USER_DEVICES WHERE USER_ID IN (SELECT ID FROM USERS WHERE EMAIL IN ($placeholders))",
                *emails
            )
            jdbcTemplate.update(
                "DELETE FROM USER_ALARM_SETTINGS WHERE USER_ID IN (SELECT ID FROM USERS WHERE EMAIL IN ($placeholders))",
                *emails
            )
            jdbcTemplate.update("DELETE FROM USERS WHERE EMAIL IN ($placeholders)", *emails)
        }

        fun deleteSignUpApplicationsByEmails(vararg emails: String) {
            val placeholders = emails.joinToString(",") { "?" }
            jdbcTemplate.update(
                """
                DELETE FROM SIGN_UP_APPLICATION_ACTIVITY_UNIT
                WHERE APPLICATION_ID IN (
                    SELECT ID
                    FROM SIGN_UP_APPLICATION
                    WHERE APPLICANT_EMAIL IN ($placeholders)
                )
                """.trimIndent(),
                *emails
            )
            jdbcTemplate.update("DELETE FROM SIGN_UP_APPLICATION WHERE APPLICANT_EMAIL IN ($placeholders)", *emails)
        }

        afterTest {
            deleteUsersByEmails("concurrency-test", "abcde@gmail.com")
            deleteSignUpApplicationsByEmails("concurrency-test", "abcde@gmail.com")
        }

        feature("동시에 같은 이메일로 회원 가입을 처리한다") {

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
                countUsersByEmail("concurrency-test") shouldBe 1
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
                countSignUpApplicationsByEmail("abcde@gmail.com") shouldBe 1
            }
        }
    })
