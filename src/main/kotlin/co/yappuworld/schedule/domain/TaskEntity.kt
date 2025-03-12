package co.yappuworld.schedule.domain

import java.time.LocalDate
import java.time.LocalTime

class TaskEntity(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate?,
    override var time: LocalTime?,
    override var endTime: LocalTime?
) : ScheduleEntity()
