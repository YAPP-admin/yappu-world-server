package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.operation.infrastructure.GenerationCommandService
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.user.domain.vo.UserError
import co.yappuworld.user.domain.vo.UserRole
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class AdminUserOperationServiceTest @Autowired constructor(
    private val configRepository: ConfigRepository,
    private val generationRepository: GenerationRepository
) : CustomDataJpaTestFeatureSpec({

        val generationFindService = GenerationFindService(generationRepository)
        val adminUserOperationService = AdminUserOperationService(
            generationActiveStateManager = GenerationActiveStateManager(generationFindService, generationRepository),
            configFindService = ConfigFindService(configRepository),
            generationFindService = generationFindService,
            generationCommandService = GenerationCommandService(generationRepository),
            generationRepository = generationRepository
        )

        feature("회원가입 인증번호 조회") {

            scenario("역할별 인증번호 설정이 누락되어 있으면 예외가 발생한다") {
                configRepository
                    .findAllByIdIn(UserRole.entries.map { it.signUpCodeKey })
                    .also { configRepository.deleteAllInBatch(it) }

                val exception = shouldThrow<BusinessException> {
                    adminUserOperationService.getSignUpAuthenticationCodes()
                }

                exception.error.code shouldBe UserError.SIGN_UP_CODE_UNREGISTERED.code
            }
        }
    })
