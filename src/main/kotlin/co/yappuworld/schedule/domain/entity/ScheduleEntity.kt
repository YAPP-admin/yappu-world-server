package co.yappuworld.schedule.domain.entity

import co.yappuworld.global.exception.BusinessException
import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.schedule.domain.ScheduleError
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(name = "schedules")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
abstract class ScheduleEntity : BaseEntity() {

    var isDeleted: Boolean = false
        protected set

    abstract val name: String

    abstract val description: String?
    abstract val place: String?

    abstract val date: LocalDate
    abstract val endDate: LocalDate

    abstract val time: LocalTime
    abstract val endTime: LocalTime
    abstract val isAllDay: Boolean

    fun delete() {
        this.isDeleted = true
    }

    protected fun checkDatetime() {
        val start = LocalDateTime.of(date, time)
        val end = LocalDateTime.of(endDate, endTime)

        if (start.isAfter(end)) {
            throw BusinessException(ScheduleError.START_DATETIME_AFTER_END_DATETIME)
        }
    }
}
