package co.yappuworld.user.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.UserFixture.getSignUpApplicationEntityFixture
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationEntity
import co.yappuworld.user.infrastructure.jpa.SignUpApplicationRepository
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class ApplicationDetailsRepositoryConverterTest @Autowired constructor(
    private val signUpApplicationRepository: SignUpApplicationRepository
) : CustomDataJpaTestFeatureSpec({

        feature("Converter 테스트") {

            scenario("UserSignUpApplication Custom Converter 정상 동작 확인") {
                val application = signUpApplicationRepository.save(getSignUpApplicationEntityFixture())

                signUpApplicationRepository
                    .findByIdOrNull(application.id)
                    .shouldNotBeNull()
                    .shouldBeInstanceOf<SignUpApplicationEntity>()
            }
        }
    })
