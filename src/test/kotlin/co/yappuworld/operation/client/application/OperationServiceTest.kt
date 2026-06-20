package co.yappuworld.operation.client.application

import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class OperationServiceTest @Autowired constructor(
    private val configRepository: ConfigRepository,
    private val generationRepository: GenerationRepository
) : CustomDataJpaTestFeatureSpec({

        val operationService = OperationService(
            configFindService = ConfigFindService(configRepository),
            generationFindService = GenerationFindService(generationRepository)
        )

        feature("기수 목록 조회") {

            scenario("전체 기수 목록을 응답 형식으로 변환한다") {
                val activeGeneration = GenerationEntity(
                    2701,
                    LocalDate.of(2027, 1, 1),
                    LocalDate.of(2027, 12, 31)
                ).apply { activate() }

                generationRepository.saveAllAndFlush(
                    listOf(
                        activeGeneration,
                        GenerationEntity(2601, null, null)
                    )
                )

                val response = operationService.getGenerations()

                response.generations.take(2).map { it.generation } shouldBe listOf(2701, 2601)
                response.generations[0].startDate shouldBe LocalDate.of(2027, 1, 1)
                response.generations[0].endDate shouldBe LocalDate.of(2027, 12, 31)
                response.generations[0].isActive shouldBe true
                response.generations[1].startDate shouldBe null
                response.generations[1].endDate shouldBe null
                response.generations[1].isActive shouldBe false
            }
        }
    })
