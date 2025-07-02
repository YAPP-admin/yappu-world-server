package co.yappuworld.schedule.infrastructure

import co.yappuworld.support.environment.CustomDataJpaTestFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.AttendanceFixture.getSessionAttendanceFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldNotBeIn
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class AttendanceCommandServiceTest @Autowired constructor(
    private val attendanceRepository: AttendanceRepository
) : CustomDataJpaTestFeatureSpec({

        val attendanceCommandService = AttendanceCommandService(attendanceRepository)

        feature("도메인 모델로부터 새로운 출석 정보를 저장") {

            scenario("출석 정보가 있으면 정상적으로 저장된다.") {
                val attendance = getAttendanceEntityFixture()

                shouldNotThrowAny {
                    attendanceCommandService.checkIn(
                        getSessionAttendanceFixture(attendance = attendance)
                    )
                }

                attendanceRepository
                    .findByUserIdAndScheduleId(
                        userId = attendance.userId,
                        scheduleId = attendance.scheduleId
                    ).shouldNotBeNull()
            }
        }

        feature("여러 세션 목록에 매칭되는 출석 정보들을 삭제한다.") {

            scenario("삭제할 출석 정보가 없으면 예외가 발생한다.") {
                shouldThrowExactly<IllegalArgumentException> {
                    attendanceCommandService.deleteAllInSessions(emptyList())
                }.message shouldBe "삭제를 위한 세션 ID는 적어도 하나 이상이어야 합니다."
            }

            scenario("세션이 여러개면, 해당 세션의 모든 출석 정보를 삭제한다.") {
                val sessionIds = List(2) { UUID.randomUUID() }
                attendanceRepository.saveAllAndFlush(
                    sessionIds.map { getAttendanceEntityFixture(scheduleId = it) }
                )

                attendanceCommandService.deleteAllInSessions(sessionIds)

                val result = attendanceRepository.findAll()
                result.shouldForAll { it.id shouldNotBeIn sessionIds }
            }
        }
    })
