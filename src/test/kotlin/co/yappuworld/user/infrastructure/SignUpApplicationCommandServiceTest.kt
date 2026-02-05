package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationActivityUnitRepository
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class SignUpApplicationCommandServiceTest @Autowired constructor(
    private val signUpApplicationRepository: SignUpApplicationRepository,
    private val signUpApplicationActivityUnitRepository: SignUpApplicationActivityUnitRepository
) : CustomDataJpaTestFeatureSpec({
        val signUpApplicationCommandService =
            SignUpApplicationCommandService(signUpApplicationRepository, signUpApplicationActivityUnitRepository)

        feature("회원 가입 신청서 제출") {

            scenario("신청서를 제출하고 조회가 가능하다.") {
                val signUpApplication = getSignUpApplicationEntityFixture()
                signUpApplicationCommandService.submit(signUpApplication)

                signUpApplicationRepository.flush()

                signUpApplicationRepository.findByIdOrNull(signUpApplication.id).shouldNotBeNull()
            }
        }
    })
