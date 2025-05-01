package co.yappuworld.schedule.domain.entity

import co.yappuworld.global.util.TimeUtils.LocalDateTimeRange
import co.yappuworld.schedule.domain.SessionType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit.SECONDS

@Entity
@DiscriminatorValue(value = "SESSION")
class SessionEntity(
    override var name: String,
    override var description: String?,
    override var place: String?,
    override var date: LocalDate,
    override var endDate: LocalDate,
    override var time: LocalTime,
    override var endTime: LocalTime,
    override var isAllDay: Boolean,
    generation: Int,
    sessionType: SessionType
) : ScheduleEntity() {

    init {
        checkDatetime()
    }

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
        endDate: LocalDate,
        time: LocalTime,
        endTime: LocalTime,
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

    val checkInTimeFrom: LocalDateTime
        get() = LocalDateTime.of(date, time).minusMinutes(20)

    val checkInTimeUntil: LocalDateTime
        get() = LocalDateTime.of(date, endTime)

    val checkInRange: LocalDateTimeRange
        get() = LocalDateTimeRange(
            start = checkInTimeFrom,
            endExclusive = checkInTimeUntil,
            unit = SECONDS
        )

    val lateTimeFrom: LocalDateTime
        get() = LocalDateTime.of(date, time).plusMinutes(20)

    val lateTimeUntil: LocalDateTime
        get() = LocalDateTime.of(date, time).plusHours(2)

    val lateTimeRange: LocalDateTimeRange
        get() = LocalDateTimeRange(
            start = lateTimeFrom,
            endExclusive = lateTimeUntil,
            unit = SECONDS
        )

    fun isFinished(now: LocalDateTime): Boolean =
        endDate.isBefore(now.toLocalDate()) ||
            (endDate.isEqual(now.toLocalDate()) && (endTime?.isBefore(now.toLocalTime()) == true))
}
