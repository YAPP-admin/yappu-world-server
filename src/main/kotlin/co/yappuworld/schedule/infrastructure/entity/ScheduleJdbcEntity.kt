package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import co.yappuworld.schedule.domain.ScheduleType
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Table("schedules")
abstract class ScheduleJdbcEntity : BaseEntity() {

    protected var isDeleted: Boolean = false

    protected abstract val name: String

    protected abstract var description: String?
    protected abstract var place: String?

    protected abstract var date: LocalDate
    protected abstract var endDate: LocalDate?
    protected abstract var time: LocalTime?
    protected abstract var endTime: LocalTime?

    protected abstract var type: ScheduleType

    override fun getId(): UUID = this.id

    override fun isNew(): Boolean = !isCreatedAtInitialized()
}
