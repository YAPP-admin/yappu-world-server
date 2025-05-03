package co.yappuworld.schedule.infrastructure.entity

import java.time.LocalDate
import java.time.LocalTime

class TaskEntity(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate,
    override var time: LocalTime,
    override var endTime: LocalTime,
    override var isAllDay: Boolean
) : ScheduleEntity() {

    init {
        checkDatetime()
    }
}
