package co.yappuworld.schedule.domain

import co.yappuworld.global.persistence.BaseJpaEntity
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.time.LocalDate
import java.time.LocalTime

@Entity
@Table(name = "schedules")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
abstract class ScheduleEntity : BaseJpaEntity() {

    protected var isDeleted: Boolean = false

    protected abstract var name: String

    protected abstract var description: String?
    protected abstract var place: String?

    protected abstract var date: LocalDate
    protected abstract var endDate: LocalDate?
    protected abstract var time: LocalTime?
    protected abstract var endTime: LocalTime?
}
