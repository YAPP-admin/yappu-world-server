package co.yappuworld.schedule.client.application

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.AttendanceRepository
import co.yappuworld.support.environment.SpringBootTestFeatureSpec
import co.yappuworld.support.fixture.ScheduleDtoFixture.getAdminSessionCreateRequestFixture
import co.yappuworld.support.fixture.UserFixture.getUserEntityFixture
import co.yappuworld.user.infrastructure.jpa.UserRepository
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired

class ScheduleAdminServiceTest @Autowired constructor(
    private val scheduleAdminService: ScheduleAdminService,
    private val userRepository: UserRepository,
    private val attendanceRepository: AttendanceRepository
) : SpringBootTestFeatureSpec({

        feature("세션 생성") {

            scenario("세션을 생성하면 해당 세션에 참석하는 유저들의 출석 정보가 생성된다") {
                // given
                val users = userRepository.saveAll(listOf(getUserEntityFixture(), getUserEntityFixture()))
                val request = getAdminSessionCreateRequestFixture(attendeeIds = users.map { it.id })

                // when
                val scheduleId = scheduleAdminService.createSchedule(request)

                val userIds = users.map { user -> user.id }
                val result = attendanceRepository.findAllByScheduleId(scheduleId)
                result.shouldHaveSize(2)
                result.shouldForAll {
                    it.userId in userIds
                    it.status shouldBe AttendanceStatus.PENDING
                }
            }
        }
    })
