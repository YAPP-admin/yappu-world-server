package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import org.assertj.core.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull

class ScheduleRepositoryTest @Autowired constructor(
    private val scheduleRepository: ScheduleRepository
) : CustomDataJpaTestFeatureSpec({

        feature("ScheduleRepository JPA 테스트") {

            scenario("세션 타입의 데이터도 잘 저장이 된다.") {
                val session = getSessionEntityFixture()
                shouldNotThrowAny { scheduleRepository.save(session) }
            }

            scenario("JPA가 알아서 구현체로 타입을 조회한다.") {
                val session = getSessionEntityFixture()
                scheduleRepository.save(session)

                shouldNotThrowAny {
                    val schedule = scheduleRepository.findByIdOrNull(session.id)!!
                    Assertions.assertThat(schedule).isInstanceOf(SessionEntity::class.java)
                }
            }
        }
    })
