package co.yappuworld.operation.infrastructure

import co.yappuworld.support.fixture.operation.OperationFixture
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GenerationRepositoryCallbackTest {

    @Autowired
    private lateinit var generationRepository: GenerationRepository

    @Test
    fun `저장 전과 후에 isNew 필드가 다르다`() {
        val generation = OperationFixture.getGenerationFixture(value = Int.MAX_VALUE)
        assertTrue { generation.isNew }

        generationRepository.save(generation)
        assertFalse { generation.isNew }
    }

    @Test
    fun `조회 해온 것도 isNew가 False로 되어 있다`() {
        val generation = OperationFixture.getGenerationFixture(value = Int.MAX_VALUE)
        generationRepository.save(generation)

        val findGeneration = generationRepository.findByIdOrNull(generation.id)!!
        assertFalse { findGeneration.isNew }
    }
}
