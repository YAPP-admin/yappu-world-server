package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.client.application.usecase.UserLoginPermissionChecker
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class UserLoginPermissionCheckerTest :
    FeatureSpec({

        val signUpApplicationFindService = mockk<SignUpApplicationFindService>()
        val userLoginPermissionChecker = UserLoginPermissionChecker(signUpApplicationFindService)

        feature("로그인 권한 체크") {

            feature("유저가 존재하지 않는 경우") {

                val user = null
                val email = "email1234@email.com"
                val password = "Password12!"

                scenario("기존에 가입 시도가 없는 경우") {
                    every { signUpApplicationFindService.findLatestSignUpApplication(any()) } returns null

                    shouldThrowExactly<BusinessException> {
                        userLoginPermissionChecker.checkLoginAvailability(user, email, password)
                    }.error shouldBe UserError.FAIL_LOGIN_NOT_FOUND_USER
                }

                scenario("기존에 가입 신청 상태에 따라 예외가 다르다.") {
                    forAll(
                        row(
                            SignUpApplicationStatus.PENDING,
                            UserError.CANNOT_LOGIN_WITH_UNPROCESSED_SIGN_UP_APPLICATION
                        ),
                        row(SignUpApplicationStatus.APPROVED, UserError.CANNOT_LOGIN_WRONG_USER_STATE),
                        row(SignUpApplicationStatus.REJECTED, UserError.RECENT_SIGN_UP_APPLICATION_REJECTED)
                    ) { status, error ->
                        every { signUpApplicationFindService.findLatestSignUpApplication(any()) } returns
                            getSignUpApplicationEntityFixture(status = status)

                        shouldThrowExactly<BusinessException> {
                            userLoginPermissionChecker.checkLoginAvailability(user, email, password)
                        }.error shouldBe error
                    }
                }
            }

            feature("유저가 존재하는 경우") {

                val email = "email1234@email.com"
                val password = "Password12!"
                val user = getUserEntityFixture(email = email, plainPassword = password)

                scenario("비밀번호가 틀리면 예외가 발생한다.") {
                    val wrongPassword = password + "1"

                    shouldThrowExactly<BusinessException> {
                        userLoginPermissionChecker.checkLoginAvailability(user, email, wrongPassword)
                    }.error shouldBe UserError.WRONG_LOGIN_USER_INFORMATION
                }

                scenario("탈퇴한 유저인 경우 로그인이 불가하다.") {
                    val withdrawnUser = getUserEntityFixture(email = email, plainPassword = password)
                        .apply { withdraw() }

                    shouldThrowExactly<BusinessException> {
                        userLoginPermissionChecker.checkLoginAvailability(withdrawnUser, email, password)
                    }.error shouldBe UserError.WITHDRAWN_USER
                }
            }
        }
    })
