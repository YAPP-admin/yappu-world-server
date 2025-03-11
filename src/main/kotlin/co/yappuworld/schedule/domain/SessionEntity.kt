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
    val generation: Int,
    @Enumerated(EnumType.STRING)
    val sessionType: SessionType
) : ScheduleEntity()
