package co.yappuworld.schedule.domain

import org.springframework.data.domain.Persistable
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class Session private constructor(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate?,
    override var time: LocalTime,
    override var endTime: LocalTime?,
    override var type: ScheduleType
) : Schedule(), Persistable<UUID> {

    constructor(
        name: String,
        description: String?,
        place: String?,
        date: LocalDate,
        endDate: LocalDate?,
        time: LocalTime,
        endTime: LocalTime?
    ) : this(
        name,
        description,
        place,
        date,
        endDate,
        time,
        endTime,
        ScheduleType.SESSION
    )

    fun withId(id: UUID): Session {
        return Session(name, description, place, date, endDate, time, endTime, type).apply {
            this.id = id
        }
    }

    override fun getId(): UUID {
        return this.id
    }

    override fun isNew(): Boolean {
        return !isCreatedAtInitialized()
    }
}
