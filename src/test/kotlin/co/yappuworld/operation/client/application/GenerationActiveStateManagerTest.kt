package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class GenerationActiveStateManagerTest @Autowired constructor(
    private val repository: GenerationRepository
) : CustomDataJpaTestFeatureSpec({

        lateinit var generationActiveStateManager: GenerationActiveStateManager
        beforeEach {
            generationActiveStateManager = GenerationActiveStateManager(repository)
        }

        feature("기수 활성화 상태 관리") {

            scenario("존재하지 않는 기수를 활성화 시키면 예외가 발생한다.") {
                val generationValue = Int.MAX_VALUE
                repository.findByIdOrNull(generationValue).shouldBeNull()
                shouldThrowExactly<BusinessException> { generationActiveStateManager.activate(generationValue) }
            }

            scenario("특정 기수를 비활성화 하면 모두 비활성화 된다.") {
                generationActiveStateManager.activate(23)
                generationActiveStateManager.activate(24)
                generationActiveStateManager.activate(25)

                repository
                    .findByIdOrNull(23)
                    .shouldNotBeNull()
                    .isActive
                    .shouldBeFalse()
                repository
                    .findByIdOrNull(24)
                    .shouldNotBeNull()
                    .isActive
                    .shouldBeFalse()
                repository
                    .findByIdOrNull(25)
                    .shouldNotBeNull()
                    .isActive
                    .shouldBeTrue()
            }

            scenario("특정 기수를 비활성화 시키면 해당 기수만 비활성화 된다.") {
                generationActiveStateManager.activate(25)
                repository
                    .findByIdOrNull(25)
                    .shouldNotBeNull()
                    .isActive
                    .shouldBeTrue()

                generationActiveStateManager.deactivate(25)
                repository
                    .findByIdOrNull(25)
                    .shouldNotBeNull()
                    .isActive
                    .shouldBeFalse()
            }

            scenario("활성화 되어 있는 기수가 있다면 해당 기수의 값을 반환한다.") {
                generationActiveStateManager.activate(25)
                generationActiveStateManager.getActiveGenerationOrNull() shouldBe 25
            }
        }
    })
