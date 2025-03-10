package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.domain.SessionEntity
import co.yappuworld.schedule.infrastructure.repository.ScheduleJpaRepository
import co.yappuworld.support.fixture.schedule.ScheduleFixture
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.Test

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScheduleRepositoryTest {

    @Autowired
    lateinit var scheduleRepository: ScheduleJpaRepository

    @Test
    fun `Session 타입의 데이터도 잘 저장이 된다`() {
        val session = ScheduleFixture.getSessionFixture()
        assertDoesNotThrow {
            scheduleRepository.save(session)
        }
    }

    @Test
    fun `JPA가 알아서 구현체로 타입을 조회한다`() {
        val session = ScheduleFixture.getSessionFixture()
        scheduleRepository.save(session)

        assertDoesNotThrow {
            val schedule = scheduleRepository.findByIdOrNull(session.id)!!
            Assertions.assertThat(schedule).isInstanceOf(SessionEntity::class.java)
        }
    }
}
