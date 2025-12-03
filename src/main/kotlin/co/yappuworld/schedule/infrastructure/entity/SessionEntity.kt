package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.converter.LocalDateStringConverter
import co.yappuworld.global.persistence.converter.LocalTimeStringConverter
import co.yappuworld.global.util.DatetimeUtils.korean
import co.yappuworld.schedule.domain.AttendancePolicy.ABSENT_AFTER_SESSION_START_HOURS
import co.yappuworld.schedule.domain.AttendancePolicy.CHECK_IN_AVAILABLE_BEFORE_SESSION_START_MINUTES
import co.yappuworld.schedule.domain.AttendancePolicy.LATE_AFTER_SESSION_START_MINUTES
import co.yappuworld.schedule.domain.vo.AttendanceError
import co.yappuworld.schedule.domain.vo.AttendanceStatus
import co.yappuworld.schedule.domain.vo.SessionType
import jakarta.persistence.Column
import jakarta.persistence.Convert
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
    override var address: String? = null,
    override var latitude: Double? = null,
    override var longitude: Double? = null,
    @field:Column(name = "START_DATE")
    @get:Convert(converter = LocalDateStringConverter::class)
    override var date: LocalDate,
    @field:Column(name = "END_DATE")
    @get:Convert(converter = LocalDateStringConverter::class)
    override var endDate: LocalDate,
    @field:Column(name = "START_TIME")
    @get:Convert(converter = LocalTimeStringConverter::class)
    override var time: LocalTime,
    @field:Column(name = "END_TIME")
    @get:Convert(converter = LocalTimeStringConverter::class)
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

    val startDayOfWeek: String
        get() = date.dayOfWeek.korean()

    val endDayOfWeek: String
        get() = endDate.dayOfWeek.korean()

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
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.date = date
        this.endDate = endDate
        this.time = time
        this.endTime = endTime
        this.generation = generation
        this.sessionType = sessionType
    }

    val checkInTimeFrom: LocalDateTime
        get() = LocalDateTime.of(date, time).minusMinutes(CHECK_IN_AVAILABLE_BEFORE_SESSION_START_MINUTES)

    val checkInTimeUntil: LocalDateTime
        get() = LocalDateTime.of(endDate, endTime)

    val lateTimeFrom: LocalDateTime
        get() = LocalDateTime.of(date, time).plusMinutes(LATE_AFTER_SESSION_START_MINUTES)

    val lateTimeUntil: LocalDateTime
        get() = LocalDateTime.of(date, time).plusHours(ABSENT_AFTER_SESSION_START_HOURS)

    fun decideCheckInStatus(now: LocalDateTime): AttendanceStatus =
        when {
            now.isBefore(checkInTimeFrom) -> throw BusinessException(AttendanceError.NOT_CHECK_IN_TIME)
            now.isBefore(lateTimeFrom) -> AttendanceStatus.ON_TIME
            now.isBefore(lateTimeUntil) -> AttendanceStatus.LATE
            else -> AttendanceStatus.ABSENT
        }
}
