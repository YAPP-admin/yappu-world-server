package co.yappuworld.user.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.security.JwtGenerator
import co.yappuworld.global.security.JwtProperties
import co.yappuworld.global.security.JwtResolver
import co.yappuworld.support.fixture.UserDtoFixture.getLatestSignUpApplicationApiRequestFixture
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.client.application.UserAuthService
import co.yappuworld.user.client.application.usecase.UserLoginPermissionChecker
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.infrastructure.SignUpApplicationFindService
import co.yappuworld.user.infrastructure.UserFindService
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class UserAuthServiceTest :
    FeatureSpec({

        val jwtProperties = JwtProperties(
            secretKey = "thisisforlocalsfjweifjweifiewfuwefewmkcewocfweklocalsecretkeyonlyusinginlocalenvironment",
            accessTokenExpirationTimes = 1000, // 1초
            refreshTokenExpirationTimes = 2000
        )
        val jwtGenerator = JwtGenerator(jwtProperties)
        val jwtResolver = JwtResolver(jwtProperties)
        val userFindService = mockk<UserFindService>()
        val signUpApplicationFindService = mockk<SignUpApplicationFindService>()
        val userLoginPermissionChecker = mockk<UserLoginPermissionChecker>()

        val userAuthService = UserAuthService(
            jwtGenerator = jwtGenerator,
            jwtResolver = jwtResolver,
            userFindService = userFindService,
            signUpApplicationFindService = signUpApplicationFindService,
            userLoginPermissionChecker = userLoginPermissionChecker
        )

        feature("가장 최근의 가입 신청서 조회") {

            scenario("가입 신청서가 존재하지 않으면 예외 발생") {
                every { signUpApplicationFindService.findLatestSignUpApplication(any()) } returns null

                val request = getLatestSignUpApplicationApiRequestFixture()
                shouldThrowExactly<BusinessException> { userAuthService.findLatestSignUpApplication(request) }
                    .error shouldBe UserError.NO_SIGN_UP_APPLICATION
            }

            scenario("가입 신청서의 비밀번호가 일치하지 않으면 예외 발생") {
                val request = getLatestSignUpApplicationApiRequestFixture(password = "Password12@")
                val signUpApplication = getSignUpApplicationEntityFixture(
                    plainPassword = "Password12!"
                )
                every { signUpApplicationFindService.findLatestSignUpApplication(request.email) } returns
                    signUpApplication

                shouldThrowExactly<BusinessException> {
                    userAuthService.findLatestSignUpApplication(request)
                }.error shouldBe UserError.MISMATCH_REQUEST_AND_SIGN_UP_APPLICATION
            }

            scenario("가입 신청서의 상태에 따라 반환 값의 상태가 결정된다.") {
                SignUpApplicationStatus.entries.forAll { status ->
                    val request = getLatestSignUpApplicationApiRequestFixture()
                    val signUpApplication = getSignUpApplicationEntityFixture(
                        email = request.email,
                        plainPassword = request.password,
                        status = status
                    )
                    every { signUpApplicationFindService.findLatestSignUpApplication(request.email) } returns
                        signUpApplication

                    val result = userAuthService.findLatestSignUpApplication(request)
                    result.status shouldBe status
                }
            }
        }
    })
