package co.yappuworld.schedule.domain

import co.yappuworld.global.persistence.BaseEntity
import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.data.annotation.Id
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

@Table("schedules")
abstract class Schedule :
    BaseEntity(),
    Persistable<UUID> {
    @Id
    @JvmField
    protected var id: UUID = UlidCreator.getMonotonicUlid().toUuid()
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
