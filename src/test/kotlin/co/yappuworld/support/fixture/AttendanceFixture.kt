package co.yappuworld.support.fixture

import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.domain.AttendanceBook
import co.yappuworld.schedule.domain.Attendee
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.infrastructure.jpa.AttendanceEntity
import co.yappuworld.schedule.infrastructure.jpa.SessionEntity
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitsFixture
import co.yappuworld.user.domain.model.UserWithActivityUnits
import co.yappuworld.user.domain.vo.Position
import co.yappuworld.user.domain.vo.UserRole
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.time.LocalDateTime
import java.util.UUID

object AttendanceFixture {

    fun getSessionAttendanceFixture(
        attendee: Attendee = getAttendeeFixture(getUserWithActivityUnitFixture()),
        session: SessionEntity = getSessionEntityFixture(),
        attendance: AttendanceEntity? = null
    ): SessionAttendance =
        SessionAttendance(
            attendee = attendee,
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
        attendees: List<Attendee> = listOf(getAttendeeFixture(getUserWithActivityUnitFixture())),
        sessions: List<SessionEntity> = listOf(getSessionEntityFixture(generation = generation)),
        attendances: List<AttendanceEntity> = emptyList(),
        latePassCountByUserId: Map<UUID, Int> = emptyMap(),
        now: LocalDateTime = LocalDateTime.of(2025, 2, 10, 0, 0)
    ): AttendanceBook =
        AttendanceBook(
            generation = generation,
            attendees = attendees,
            sessions = sessions,
            attendances = attendances,
            latePassCountByUserId = latePassCountByUserId,
            now = now
        )

    fun getAttendeeFixture(
        id: UUID = UUID.randomUUID(),
        name: String = "test",
        role: UserRole = UserRole.ACTIVE,
        position: Position = Position.PM
    ): Attendee =
        Attendee(
            id = id,
            name = name,
            role = role,
            position = position
        )

    fun getAttendeeFixture(
        userWithActivityUnit: UserWithActivityUnit = getUserWithActivityUnitFixture(),
        generation: Int = 25
    ): Attendee =
        Attendee.from(
            userWithActivityUnit = userWithActivityUnit,
            generation = generation
        )

    fun getAttendeeFixture(
        userWithActivityUnits: UserWithActivityUnits = getUserWithActivityUnitsFixture(),
        generation: Int = 25
    ): Attendee =
        Attendee.from(
            userWithActivityUnits = userWithActivityUnits,
            generation = generation
        )
}
