package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class GenerationActiveStateManagerTest @Autowired constructor(
    private val repository: GenerationRepository
) : CustomDataJpaTestFeatureSpec({

        val generationFindService = GenerationFindService(repository)
        val generationActiveStateManager = GenerationActiveStateManager(generationFindService, repository)

        beforeEach {
            fun saveNotExist(generation: GenerationEntity) {
                repository.findByIdOrNull(generation.id) ?: repository.saveAndFlush(generation)
            }

            listOf(
                GenerationEntity(23, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)),
                GenerationEntity(24, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)),
                GenerationEntity(25, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31))
            ).forEach { saveNotExist(it) }
        }

        feature("기수 활성화 관리") {

            scenario("존재하지 않는 기수를 활성화 시키면 예외가 발생한다") {
                val generationValue = Int.MAX_VALUE
                repository.findByIdOrNull(generationValue).shouldBeNull()
                shouldThrow<BusinessException> { generationActiveStateManager.activate(generationValue) }
            }

            scenario("특정 기수를 활성화 시키면 나머지는 모두 비활성화 된다") {
                generationActiveStateManager.activate(23)
                generationActiveStateManager.activate(24)
                generationActiveStateManager.activate(25)

                repository.findByIdOrNull(23)?.isActive?.shouldBeFalse()
                repository.findByIdOrNull(24)?.isActive?.shouldBeFalse()
                repository.findByIdOrNull(25)?.isActive?.shouldBeTrue()
            }

            scenario("특정 기수를 비활성화 시키면 해당 기수만 비활성화 된다") {
                generationActiveStateManager.activate(25)
                repository.findByIdOrNull(25)?.isActive?.shouldBeTrue()

                generationActiveStateManager.deactivate(25)
                repository.findByIdOrNull(25)?.isActive?.shouldBeFalse()
            }

            scenario("활성화 되어 있는 기수가 있다면 해당 기수의 값을 반환한다") {
                generationActiveStateManager.activate(25)
                generationActiveStateManager.getActiveGenerationOrNull() shouldBe 25
            }
        }
    })
