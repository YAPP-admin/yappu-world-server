package co.yappuworld.support.fixture

import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.infrastructure.entity.AttendanceEntity
import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.SessionAttendance
import co.yappuworld.schedule.infrastructure.entity.SessionEntity
import co.yappuworld.support.fixture.ScheduleFixture.getSessionEntityFixture
import co.yappuworld.support.fixture.UserFixture.getUserWithActivityUnitFixture
import co.yappuworld.user.infrastructure.model.UserWithActivityUnit
import java.util.UUID

object AttendanceFixture {

    fun getSessionAttendanceFixture(
        user: UserWithActivityUnit = getUserWithActivityUnitFixture(),
        session: SessionEntity = getSessionEntityFixture(),
        attendance: AttendanceEntity? = null
    ): SessionAttendance =
        SessionAttendance(
            user = user,
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
}
