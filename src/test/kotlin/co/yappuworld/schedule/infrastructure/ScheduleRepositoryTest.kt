package co.yappuworld.schedule.infrastructure

import co.yappuworld.support.fixture.schedule.ScheduleFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.data.mapping.model.MappingInstantiationException
import kotlin.jvm.optionals.getOrNull
import kotlin.test.Test

@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ScheduleRepositoryTest {

    @Autowired
    lateinit var scheduleRepository: ScheduleRepository

    @Autowired
    lateinit var sessionRepository: SessionRepository

    @Test
    fun `Session 타입의 데이터도 잘 저장이 된다`() {
        val session = ScheduleFixture.getSessionFixture()
        assertDoesNotThrow {
            scheduleRepository.save(session)
        }
    }

    @Test
    fun `ScheduleRepository를 사용하면 추상 클래스 인스턴스화가 안 되어 조회가 불가하다`() {
        val session = ScheduleFixture.getSessionFixture()
        scheduleRepository.save(session)

        assertThrows<MappingInstantiationException> {
            scheduleRepository.findById(session.id)
        }
    }

    @Test
    @Disabled
    fun `조회 시에는 구체 클래스의 Repository를 이용해야 한다`() {
        val session = ScheduleFixture.getSessionFixture()
        scheduleRepository.save(session)

        assertDoesNotThrow {
            val findSession = sessionRepository.findById(session.id)
            assertThat(findSession.getOrNull()?.id).isEqualTo(session.id)
        }
    }
}
