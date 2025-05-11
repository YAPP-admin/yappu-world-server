package co.yappuworld.support.fixture

import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.time.LocalDateTime
import java.util.UUID

object AttendanceFixture {

    fun getSessionAttendanceFixture(
        user: UserWithActivityUnit = getUserWithActivityUnitFixture(),
        session: SessionEntity = getSessionEntityFixture(),
        attendance: AttendanceEntity? = null
    ): SessionAttendance =
        SessionAttendance(
            attendee = user,
            session = session,
            attendance = attendance
        )

    fun getAttendRequestFixture(
        attendanceCode: String = "1234",
        sessionId: UUID = UUID.randomUUID()
    ): AttendanceRequest =
        AttendanceRequest(
            attendanceCode = attendanceCode,
            sessionId = sessionId
        )

    fun getAttendanceEntityFixture(
        status: AttendanceStatus = AttendanceStatus.ON_TIME,
        userId: UUID = UUID.randomUUID(),
        scheduleId: UUID = UUID.randomUUID()
    ) = AttendanceEntity(
        status = status,
        userId = userId,
        scheduleId = scheduleId
    )

    fun getAttendanceBookFixture(
        generation: Int = 25,
        users: List<UserWithActivityUnit> = listOf(getUserWithActivityUnitFixture(generation = generation)),
        sessions: List<SessionEntity> = listOf(getSessionEntityFixture(generation = generation)),
        attendances: List<AttendanceEntity> = emptyList(),
        latePassCountByUserId: Map<UUID, Int> = emptyMap(),
        now: LocalDateTime = LocalDateTime.of(2025, 2, 10, 0, 0)
    ): AttendanceBook =
        AttendanceBook(
            generation = generation,
            users = users,
            sessions = sessions,
            attendances = attendances,
            latePassCountByUserId = latePassCountByUserId,
            now = now
        )
}
