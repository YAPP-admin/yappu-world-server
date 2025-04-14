package co.yappuworld.support.fixture

import co.yappuworld.attendance.client.dto.request.AttendanceRequest
import co.yappuworld.attendance.domain.Attendance
import co.yappuworld.attendance.domain.AttendanceStatus
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

    fun getAttendanceFixture(
        status: AttendanceStatus = AttendanceStatus.ON_TIME,
        userId: UUID = UUID.randomUUID(),
        scheduleId: UUID = UUID.randomUUID()
    ) = Attendance(
        status = status,
        userId = userId,
        scheduleId = scheduleId
    )
}
