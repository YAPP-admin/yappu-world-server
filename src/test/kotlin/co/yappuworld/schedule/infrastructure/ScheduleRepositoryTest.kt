package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.entity.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaTest
import co.yappuworld.support.fixture.ScheduleFixture
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.Test

@CustomDataJpaTest
class ScheduleRepositoryTest {

    @Autowired
    lateinit var scheduleRepository: ScheduleRepository

    @Test
    fun `Session 타입의 데이터도 잘 저장이 된다`() {
        val session = ScheduleFixture.getSessionEntityFixture()
        assertDoesNotThrow {
            scheduleRepository.save(session)
        }
    }

    @Test
    fun `JPA가 알아서 구현체로 타입을 조회한다`() {
        val session = ScheduleFixture.getSessionEntityFixture()
        scheduleRepository.save(session)

        assertDoesNotThrow {
            val schedule = scheduleRepository.findByIdOrNull(session.id)!!
            Assertions.assertThat(schedule).isInstanceOf(SessionEntity::class.java)
        }
    }
}
