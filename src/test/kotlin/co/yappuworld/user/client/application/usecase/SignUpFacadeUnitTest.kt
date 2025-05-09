package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.SignUpApplicationCommandService
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import co.yappuworld.user.infrastructure.UserCommandService
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.inspectors.forAll
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

class SignUpFacadeUnitTest :
    FeatureSpec({
        val userFindService = mockk<UserFindService>()
        val userCommandService = mockk<UserCommandService>()
        val signUpApplicationFindService = mockk<SignUpApplicationFindService>()
        val signUpApplicationCommandService = mockk<SignUpApplicationCommandService>()
        val jwtGenerator = mockk<JwtGenerator>()

        val executor = SignUpExecutor(
            userFindService,
            userCommandService,
            signUpApplicationFindService,
            signUpApplicationCommandService,
            jwtGenerator
        )

        feature("가입 신청서 제출") {

            every { signUpApplicationCommandService.getLock(any()) } returns 1

            scenario("이미 사용 중인 이메일이면 제출할 수 없다.") {
                every { userFindService.existsEmail(any()) } returns true
                shouldThrowExactly<BusinessException> {
                    executor.submit(getSignUpApplicationEntityFixture())
                }.error shouldBe UserError.ALREADY_SIGNED_UP_EMAIL
            }

            scenario("이미 PENDING 상태의 신청서가 있으면 추가 제출이 불가") {
                every { userFindService.existsEmail(any()) } returns false
                every { signUpApplicationFindService.existsPendingApplication(any()) } returns true

                shouldThrowExactly<BusinessException> {
                    executor.submit(getSignUpApplicationEntityFixture())
                }.error shouldBe UserError.UNPROCESSED_APPLICATION_EXISTS
            }

            scenario("정상적으로 신청서를 제출한다.") {
                every { userFindService.existsEmail(any()) } returns false
                every { signUpApplicationFindService.existsPendingApplication(any()) } returns false
                justRun { signUpApplicationCommandService.submit(any()) }

                executor.submit(getSignUpApplicationEntityFixture())

                verify(exactly = 1) { signUpApplicationCommandService.submit(any()) }
            }
        }

        feature("가입 신청서 승인") {

            scenario("존재하지 않는 ID만 있으면 실패한다.") {
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                    emptyList()

                shouldThrowExactly<BusinessException> {
                    executor.approve(listOf(UUID.randomUUID()), UserRole.ACTIVE)
                }.error shouldBe UserError.CONTAIN_NOT_EXIST_APPLICATION_ID
            }

            scenario("일부 존재하지 않는 ID가 있으면 실패한다.") {
                val application = getSignUpApplicationEntityFixture()
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                    listOf(application)

                shouldThrowExactly<BusinessException> {
                    executor.approve(listOf(UUID.randomUUID(), application.id), UserRole.ACTIVE)
                }.error shouldBe UserError.CONTAIN_NOT_EXIST_APPLICATION_ID
            }

            scenario("이미 처리된 신청서가 있으면 예외 발생") {
                forAll(
                    row(SignUpApplicationStatus.APPROVED),
                    row(SignUpApplicationStatus.REJECTED)
                ) { status ->
                    val application = getSignUpApplicationEntityFixture(status = status)
                    every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                        listOf(application)

                    shouldThrowExactly<BusinessException> {
                        executor.approve(listOf(application.id), UserRole.ACTIVE)
                    }.error shouldBe UserError.CONTAIN_ALREADY_PROCESSED_APPLICATION
                }
            }

            scenario("하나라도 이미 가입된 이메일이라면 예외가 발생한다.") {
                val applications = List(2) { getSignUpApplicationEntityFixture(email = "email$it@email.com") }
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns applications
                every { userFindService.existsEmail(applications[0].applicantEmail) } returns true
                every { userFindService.existsEmail(applications[1].applicantEmail) } returns false

                shouldThrowExactly<BusinessException> {
                    executor.approve(applications.map { it.id }, UserRole.ACTIVE)
                }.error shouldBe UserError.ALREADY_SIGNED_UP_EMAIL
            }

            scenario("정상적으로 승인된다.") {
                val applications = List(2) { getSignUpApplicationEntityFixture(email = "email$it@email.com") }
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns applications
                every { userFindService.existsEmail(any()) } returns false
                val users = List(2) { getUserEntityFixture() }
                applications.forEachIndexed { index, application ->
                    every { userCommandService.signUp(application, any()) } returns users[index]
                }

                executor.approve(applications.map { it.id }, UserRole.ACTIVE)

                applications.forEach { application ->
                    verify(exactly = 1) { userCommandService.signUp(application, any()) }
                }
                applications.forAll { it.isProcessed().shouldBeTrue() }
            }
        }

        feature("가입 신청서 거절") {

            scenario("존재하지 않는 ID만 있으면 실패한다.") {
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                    emptyList()

                shouldThrowExactly<BusinessException> {
                    executor.reject(listOf(UUID.randomUUID()))
                }.error shouldBe UserError.CONTAIN_NOT_EXIST_APPLICATION_ID
            }

            scenario("일부 존재하지 않는 ID가 있으면 실패한다.") {
                val application = getSignUpApplicationEntityFixture()
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                    listOf(application)

                shouldThrowExactly<BusinessException> {
                    executor.reject(listOf(UUID.randomUUID(), application.id))
                }.error shouldBe UserError.CONTAIN_NOT_EXIST_APPLICATION_ID
            }

            scenario("이미 처리된 신청서가 있으면 예외 발생") {
                forAll(
                    row(SignUpApplicationStatus.APPROVED),
                    row(SignUpApplicationStatus.REJECTED)
                ) { status ->
                    val application = getSignUpApplicationEntityFixture(status = status)
                    every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                        listOf(application)

                    shouldThrowExactly<BusinessException> {
                        executor.reject(listOf(application.id))
                    }.error shouldBe UserError.CONTAIN_ALREADY_PROCESSED_APPLICATION
                }
            }

            scenario("거절 처리 된다") {
                val application = getSignUpApplicationEntityFixture()
                every { signUpApplicationFindService.findSignUpApplications(ids = any()) } returns
                    listOf(application)

                executor.reject(listOf(application.id))

                application.isProcessed().shouldBeTrue()
                application.status shouldBe SignUpApplicationStatus.REJECTED
            }
        }

        feature("이메일 사용 가능 검사") {

            scenario("이미 존재한다면 예외가 발생한다.") {
                every { userFindService.existsEmail(any()) } returns true
                shouldThrowExactly<BusinessException> {
                    executor.checkEmailAvailability("email@email.com")
                }
            }

            scenario("존재하지 않으면 예외가 발생하지 않는다.") {
                every { userFindService.existsEmail(any()) } returns false
                shouldNotThrowAny {
                    executor.checkEmailAvailability("email@email.com")
                }
            }
        }
    })
