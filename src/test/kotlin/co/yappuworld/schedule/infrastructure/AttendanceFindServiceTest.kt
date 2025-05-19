package co.yappuworld.schedule.infrastructure

import co.yappuworld.schedule.infrastructure.jpa.AttendanceEntity
import co.yappuworld.schedule.infrastructure.jpa.AttendanceRepository
import co.yappuworld.schedule.infrastructure.jpa.ScheduleRepository
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import org.springframework.beans.factory.annotation.Autowired

class AttendanceFindServiceTest @Autowired constructor(
    private val sessionRepository: ScheduleRepository,
    private val attendanceRepository: AttendanceRepository
) : CustomDataJpaTestFeatureSpec({

        val attendanceFindService = AttendanceFindService(attendanceRepository)

        feature("특정 기수의 출석 정보를 조회한다.") {

            scenario("출석 정보가 없으면 빈 리스트를 반환한다.") {
                attendanceFindService
                    .findAttendancesOfGeneration(99)
                    .shouldBeEmpty()
            }

            scenario("특정 기수의 세션과 연계된 출석 정보만 조회한다.") {
                val generation = 99
                val sessions = mutableListOf<SessionEntity>()
                val attendances = mutableListOf<AttendanceEntity>()
                repeat(3) {
                    sessions.addAll(List(3) { getSessionEntityFixture(generation = generation + it) })
                }
                sessions.forEach { session ->
                    attendances.add(getAttendanceEntityFixture(scheduleId = session.id))
                }

                sessionRepository.saveAllAndFlush(sessions)
                attendanceRepository.saveAllAndFlush(attendances)

                val result = attendanceFindService.findAttendancesOfGeneration(generation)
                result.shouldHaveSize(3)
            }
        }
    })
