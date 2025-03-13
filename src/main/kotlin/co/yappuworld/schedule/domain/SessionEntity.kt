package co.yappuworld.schedule.domain

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDate
import java.time.LocalTime

@Entity
@DiscriminatorValue(value = "SESSION")
class SessionEntity(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate?,
    override var time: LocalTime?,
    override var endTime: LocalTime?,
    generation: Int,
    sessionType: SessionType
) : ScheduleEntity() {

    var generation: Int = generation
        private set

    @Enumerated(EnumType.STRING)
    var sessionType: SessionType = sessionType
        private set

    fun update(
        name: String,
        description: String?,
        place: String?,
        date: LocalDate,
        endDate: LocalDate?,
        time: LocalTime?,
        endTime: LocalTime?,
        generation: Int,
        sessionType: SessionType
    ) {
        this.name = name
        this.description = description
        this.place = place
        this.date = date
        this.endDate = endDate
        this.time = time
        this.endTime = endTime
        this.generation = generation
        this.sessionType = sessionType
    }
}
