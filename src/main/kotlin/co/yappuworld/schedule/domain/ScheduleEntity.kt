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

    val isDeleted: Boolean = false

    abstract val name: String

    abstract val description: String?
    abstract val place: String?

    abstract val date: LocalDate
    abstract val endDate: LocalDate?
    abstract val time: LocalTime?
    abstract val endTime: LocalTime?
}
