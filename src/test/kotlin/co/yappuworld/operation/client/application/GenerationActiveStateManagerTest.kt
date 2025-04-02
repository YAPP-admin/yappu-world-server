package co.yappuworld.operation.client.application

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.operation.infrastructure.GenerationRepository
import co.yappuworld.support.environment.CustomDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test

@CustomDataJpaTest
class GenerationActiveStateManagerTest {

    @Autowired
    lateinit var repository: GenerationRepository
    lateinit var generationActiveStateManager: GenerationActiveStateManager

    @BeforeEach
    fun setUp() {
        generationActiveStateManager = GenerationActiveStateManager(repository)
    }

    @Test
    fun `존재하지 않는 기수를 활성화 시키면 예외가 발생한다`() {
        val generationValue = Int.MAX_VALUE
        assertThat(repository.findByIdOrNull(generationValue)).isNull()
        assertThrows<BusinessException> { generationActiveStateManager.activate(generationValue) }
    }

    @Test
    @Transactional
    fun `특정 기수를 활성화 시키면 나머지는 모두 비활성화 된다`() {
        assertThat(generationActiveStateManager.getActiveGenerationOrNull()).isNull()

        generationActiveStateManager.activate(23)
        generationActiveStateManager.activate(24)
        generationActiveStateManager.activate(25)

        assertThat(repository.findByIdOrNull(23)?.isActive).isFalse()
        assertThat(repository.findByIdOrNull(24)?.isActive).isFalse()
        assertThat(repository.findByIdOrNull(25)?.isActive).isTrue()
    }

    @Test
    @Transactional
    fun `특정 기수를 비활성화 시키면 해당 기수만 비활성화 된다`() {
        generationActiveStateManager.activate(25)
        assertThat(repository.findByIdOrNull(25)?.isActive).isTrue()

        generationActiveStateManager.deactivate(25)
        assertThat(repository.findByIdOrNull(25)?.isActive).isFalse()
    }

    @Test
    fun `현재 활성화 되어 있는 기수가 없다면 NULL을 반환한다`() {
        assertThat(generationActiveStateManager.getActiveGenerationOrNull()).isNull()
    }

    @Test
    @Transactional
    fun `활성화 되어 있는 기수가 있다면 해당 기수의 값을 반환한다`() {
        generationActiveStateManager.activate(25)
        assertThat(generationActiveStateManager.getActiveGenerationOrNull()).isEqualTo(25)
    }
}
