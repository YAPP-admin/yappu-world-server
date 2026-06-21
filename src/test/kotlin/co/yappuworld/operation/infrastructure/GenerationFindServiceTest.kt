package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDate

class GenerationFindServiceTest @Autowired constructor(
    private val generationRepository: GenerationRepository
) : CustomDataJpaTestFeatureSpec({

        val generationFindService = GenerationFindService(generationRepository)

        feature("기수 목록 조회") {

            scenario("전체 기수 목록을 내림차순으로 조회한다") {
                generationRepository.saveAllAndFlush(
                    listOf(
                        GenerationEntity(2301, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)),
                        GenerationEntity(2501, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)),
                        GenerationEntity(2401, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31))
                    )
                )

                val generations = generationFindService.findAllGenerations()

                generations.take(3).map { it.value } shouldBe listOf(2501, 2401, 2301)
            }
        }
    })
