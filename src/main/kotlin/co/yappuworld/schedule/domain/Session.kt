package co.yappuworld.schedule.domain

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class Session private constructor(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate?,
    override var time: LocalTime?,
    override var endTime: LocalTime?,
    generation: Int,
    override var type: ScheduleType
) : Schedule() {

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

    fun withId(id: UUID): Session =
        Session(name, description, place, date, endDate, time, endTime, generation, type).apply {
            this.id = id
        }
}
