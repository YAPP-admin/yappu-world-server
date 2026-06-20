package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.client.dto.request.ForceUpdateInquiryRequest
import co.yappuworld.operation.domain.ClientPlatform
import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.operation.domain.ConfigError
import co.yappuworld.operation.domain.GenerationEntity
import co.yappuworld.operation.domain.Version
import co.yappuworld.operation.infrastructure.ConfigFindService
import co.yappuworld.operation.infrastructure.ConfigRepository
import co.yappuworld.operation.infrastructure.GenerationFindService
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate

class OperationServiceTest @Autowired constructor(
    private val configRepository: ConfigRepository,
    private val generationRepository: GenerationRepository
) : CustomDataJpaTestFeatureSpec({

        val operationService = OperationService(
            configFindService = ConfigFindService(configRepository),
            generationFindService = GenerationFindService(generationRepository)
        )

        fun saveConfig(
            id: String,
            label: String,
            category: ConfigCategory,
            value: String?
        ) {
            configRepository
                .findByIdOrNull(id)
                ?.apply { update(value) }
                ?: configRepository.save(
                    ConfigEntity(
                        name = id,
                        label = label,
                        category = category,
                        value = value
                    )
                )

            configRepository.flush()
        }

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

        feature("운영 설정 조회") {

            scenario("활성 기수 설정이 숫자가 아니면 비활성 상태를 반환한다") {
                saveConfig("activeGeneration", "활성 기수", ConfigCategory.ACTIVE_GENERATION, "invalid")

                val response = operationService.getActiveGeneration()

                response.isActive shouldBe false
                response.generation shouldBe null
            }

            scenario("최소 지원 버전 설정 형식이 잘못되면 예외가 발생한다") {
                saveConfig("minSupportVersionInAndroid", "Android 최소 지원 버전", ConfigCategory.FORCE_UPDATE, "1.a.0")

                val exception = shouldThrow<BusinessException> {
                    operationService.getForceUpdateInfo(
                        ForceUpdateInquiryRequest(
                            version = Version("1.0.0"),
                            platform = ClientPlatform.ANDROID
                        )
                    )
                }

                exception.error.code shouldBe ConfigError.WRONG_VERSION_FORMAT.code
            }
        }

    })
