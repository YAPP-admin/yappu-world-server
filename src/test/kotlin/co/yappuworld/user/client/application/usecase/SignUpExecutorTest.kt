package co.yappuworld.user.client.application.usecase

import co.yappuworld.global.util.DatetimeUtils.getCurrentDateTimeInKST
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.domain.vo.SignUpApplicationStatus
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class SignUpExecutorTest @Autowired constructor(
    private val signUpExecutor: SignUpExecutor,
    private val signUpApplicationRepository: SignUpApplicationRepository
) : SpringBootTestFeatureSpec({

        feature("가입코드를 이용한 회원가입") {

            scenario("기존에 보류 상태의 가입 신청이 있었다면, 거절 처리된다.") {
                val signUpApplication = getSignUpApplicationEntityFixture(
                    email = "email@email.com",
                    status = SignUpApplicationStatus.PENDING
                )
                signUpApplicationRepository.saveAndFlush(signUpApplication)

                signUpExecutor.signUp(
                    application = signUpApplication,
                    role = UserRole.ACTIVE,
                    now = getCurrentDateTimeInKST()
                )

                signUpApplicationRepository
                    .findByIdOrNull(signUpApplication.id)
                    .shouldNotBeNull()
                    .let { rejectedApplication ->
                        rejectedApplication.status shouldBe SignUpApplicationStatus.REJECTED
                        rejectedApplication.rejectReason shouldBe "가입 코드를 통해 회원가입을 완료하였습니다."
                    }
            }
        }

    })
