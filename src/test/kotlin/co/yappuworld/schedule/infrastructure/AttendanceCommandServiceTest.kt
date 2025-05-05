package co.yappuworld.schedule.infrastructure

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.support.environment.CustomDataJpaFeatureSpec
import co.yappuworld.support.fixture.AttendanceFixture.getAttendanceEntityFixture
import co.yappuworld.support.fixture.AttendanceFixture.getSessionAttendanceFixture
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class AttendanceCommandServiceTest @Autowired constructor(
    private val attendanceRepository: AttendanceRepository
) : CustomDataJpaFeatureSpec({

        val attendanceCommandService = AttendanceCommandService(attendanceRepository)

        feature("도메인 모델로부터 새로운 출석 정보를 저장") {

            scenario("출석 정보가 없으면 예외가 발생한다.") {
                shouldThrowExactly<BusinessException> {
                    attendanceCommandService.save(getSessionAttendanceFixture())
                }.error shouldBe AttendanceError.NO_ATTENDANCE_TO_CHECK_IN
            }

            scenario("출석 정보가 있으면 정상적으로 저장된다.") {
                val attendance = getAttendanceEntityFixture()

                shouldNotThrowAny {
                    attendanceCommandService.save(
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
    })
