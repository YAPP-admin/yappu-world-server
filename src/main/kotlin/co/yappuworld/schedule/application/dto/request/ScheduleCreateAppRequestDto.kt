package co.yappuworld.schedule.application.dto.request

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.ScheduleEntity
import co.yappuworld.schedule.domain.ScheduleError
import co.yappuworld.schedule.domain.ScheduleType
import co.yappuworld.schedule.domain.SessionEntity
import java.time.LocalDate
import java.time.LocalTime

data class ScheduleCreateAppRequestDto(
    val name: String,
    val description: String?,
    val place: String?,
    val date: LocalDate,
    val endDate: LocalDate?,
    val time: LocalTime?,
    val endTime: LocalTime?,
    val generation: Int?,
    val type: ScheduleType
) {

    fun toDomain(): ScheduleEntity =
        when (type) {
            ScheduleType.SESSION -> convertToSession()
            ScheduleType.TASK -> TODO()
            ScheduleType.ETC -> TODO()
        }

    private fun convertToSession(): ScheduleEntity =
        SessionEntity(
            name = name,
            description = description,
            place = place,
            date = date,
            endDate = endDate,
            time = time,
            endTime = endTime,
            generation = generation ?: throw BusinessException(ScheduleError.SESSION_NEED_GENERATION)
        )
}
