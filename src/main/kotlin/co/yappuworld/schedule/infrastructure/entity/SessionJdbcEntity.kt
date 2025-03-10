package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.schedule.domain.ScheduleType
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class SessionJdbcEntity private constructor(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate?,
    override var time: LocalTime?,
    override var endTime: LocalTime?,
    generation: Int,
    override var type: ScheduleType
) : ScheduleJdbcEntity() {

    var generation: Int = generation
        private set

    constructor(
        name: String,
        description: String?,
        place: String?,
        date: LocalDate,
        endDate: LocalDate?,
        time: LocalTime?,
        endTime: LocalTime?,
        generation: Int
    ) : this(
        name,
        description,
        place,
        date,
        endDate,
        time,
        endTime,
        generation,
        ScheduleType.SESSION
    )

    fun withId(id: UUID): SessionJdbcEntity =
        SessionJdbcEntity(name, description, place, date, endDate, time, endTime, generation, type).apply {
            this.id = id
        }
}
