package co.yappuworld.schedule.client.dto.response

import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.SessionProgressPhase
import co.yappuworld.support.fixture.ScheduleFixture.getSessionWithAttendanceFixture
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class AttendancesHistoryResponseV2Test :
    FeatureSpec({
        feature("출석 이력 v2 상태값 생성") {
            scenario("종료된 미출석 세션은 결석 상태를 반환한다.") {
                val now = LocalDateTime.of(2025, 2, 16, 12, 0)
                val session = getSessionWithAttendanceFixture(
                    attendanceStatus = AttendanceStatus.PENDING,
                    checkedInAt = null
                )

                AttendancesHistoryResponseV2.of(listOf(session), now).histories.single().let {
                    it.attendanceStatus shouldBe AttendanceStatus.ABSENT
                    it.progressPhase shouldBe SessionProgressPhase.DONE
                }
            }

            scenario("예정된 미출석 세션은 출석 상태 없이 진행 상태만 반환한다.") {
                val now = LocalDateTime.of(2025, 2, 14, 12, 0)
                val session = getSessionWithAttendanceFixture(
                    attendanceStatus = AttendanceStatus.PENDING,
                    checkedInAt = null
                )

                AttendancesHistoryResponseV2.of(listOf(session), now).histories.single().let {
                    it.attendanceStatus.shouldBeNull()
                    it.progressPhase shouldBe SessionProgressPhase.PENDING
                }
            }

            scenario("지각 세션은 LATE 상태를 반환한다.") {
                val now = LocalDateTime.of(2025, 2, 15, 15, 0)
                val session = getSessionWithAttendanceFixture(attendanceStatus = AttendanceStatus.LATE)

                AttendancesHistoryResponseV2.of(listOf(session), now).histories.single().let {
                    it.attendanceStatus shouldBe AttendanceStatus.LATE
                    it.progressPhase shouldBe SessionProgressPhase.ONGOING
                }
            }
        }
    })
