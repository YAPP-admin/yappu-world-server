package co.yappuworld.schedule.application.dto.request

import co.yappuworld.schedule.domain.Schedule
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.Session
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleCreateAppRequestDto(
    val name: String,
    val description: String?,
    val place: String?,
    val date: LocalDate,
    var endDate: LocalDate?,
    var time: LocalTime?,
    var endTime: LocalTime?,
    var type: ScheduleType
) {

    fun toDomain(): Schedule {
        return when (type) {
            ScheduleType.SESSION -> convertToSession()
            ScheduleType.TASK -> TODO()
            ScheduleType.ETC -> TODO()
        }
    }

    private fun convertToSession(): Session {
        return Session(
            name = name,
            description = description,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime
        )
    }
}
