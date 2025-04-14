package co.yappuworld.support.fixture

import co.yappuworld.attendance.client.dto.request.AttendanceRequest
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
}
