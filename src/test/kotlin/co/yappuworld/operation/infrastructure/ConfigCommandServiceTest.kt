package co.yappuworld.operation.infrastructure

import co.yappuworld.operation.domain.ConfigCategory
import co.yappuworld.operation.domain.ConfigEntity
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.user.domain.model.SignUpCodeBook
import co.yappuworld.user.domain.vo.UserRole
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired

class ConfigCommandServiceTest @Autowired constructor(
    private val configRepository: ConfigRepository,
    private val entityManager: EntityManager
) : CustomDataJpaTestFeatureSpec({

        val configCommandService = ConfigCommandService(configRepository)

        feature("가입코드 수정") {

            fun initializeConfigs() {
                val configs = configRepository.findAllByIdIn(UserRole.entries.map { it.signUpCodeKey })
                val signUpCodeKeys = configs.map { it.id }

                UserRole.entries
                    .filterNot { signUpCodeKeys.contains(it.signUpCodeKey) }
                    .map {
                        ConfigEntity(
                            name = it.signUpCodeKey,
                            value = it.ordinal.toString().padStart(6, '0'),
                            label = it.label,
                            category = ConfigCategory.AUTHENTICATION_CODE
                        )
                    }.also { configRepository.saveAllAndFlush(it) }
            }

            scenario("가입코드를 수정한다.") {
                initializeConfigs()

                val originConfigs = configRepository.findAllByIdIn(
                    UserRole.entries.map { it.signUpCodeKey }
                )
                val valueBySignUpCodeKey = originConfigs.associate { it.id to it.value }

                val signUpCodeBook = SignUpCodeBook(originConfigs)
                UserRole.entries.sortedBy { it.ordinal }.forEach { role ->
                    val code = signUpCodeBook.getCode(role)
                    code?.let {
                        signUpCodeBook.updateSignUpCode(
                            role,
                            (code.toInt() + 100).toString().padStart(6, '0')
                        )
                    }
                }

                configCommandService.update(signUpCodeBook)
                entityManager.flush()

                val updatedConfigs = configRepository.findAllByIdIn(UserRole.entries.map { it.signUpCodeKey })
                UserRole.entries.forEach { role ->
                    val originValue = valueBySignUpCodeKey[role.signUpCodeKey].shouldNotBeNull()
                    val updatedValue = updatedConfigs.single { it.name == role.signUpCodeKey }.value

                    updatedValue.shouldNotBeNull() shouldBe
                        (originValue.toInt() + 100).toString().padStart(6, '0')
                }
            }
        }
    })
