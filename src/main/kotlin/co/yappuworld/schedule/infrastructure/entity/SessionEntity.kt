package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.schedule.domain.AttendanceError
import co.yappuworld.schedule.domain.AttendanceStatus
import co.yappuworld.schedule.domain.SessionType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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

    val lateTimeFrom: LocalDateTime
        get() = LocalDateTime.of(date, time).plusMinutes(20)

    val lateTimeUntil: LocalDateTime
        get() = LocalDateTime.of(date, time).plusHours(2)

    fun isFinished(now: LocalDateTime): Boolean =
        endDate.isBefore(now.toLocalDate()) ||
            (endDate.isEqual(now.toLocalDate()) && (endTime?.isBefore(now.toLocalTime()) == true))

    fun decideCheckInStatus(now: LocalDateTime): AttendanceStatus =
        when {
            now.isBefore(checkInTimeFrom) -> throw BusinessException(AttendanceError.NOT_CHECK_IN_TIME)
            now.isBefore(lateTimeFrom) -> AttendanceStatus.ON_TIME
            now.isBefore(lateTimeUntil) -> AttendanceStatus.LATE
            else -> AttendanceStatus.ABSENT
        }
}
