package co.yappuworld.support.fixture

import co.yappuworld.schedule.client.dto.request.AttendanceRequest
import co.yappuworld.schedule.domain.entity.AttendanceEntity
import co.yappuworld.schedule.domain.AttendanceStatus
import java.util.UUID

object AttendanceFixture {

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
