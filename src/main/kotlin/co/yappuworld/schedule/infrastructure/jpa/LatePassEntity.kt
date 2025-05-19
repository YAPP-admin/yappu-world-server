package co.yappuworld.schedule.infrastructure.jpa

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "late_passes")
class LatePassEntity(
    val userId: UUID,
    val generation: Int,
    reason: String? = null
) : BaseEntity() {

    @Column(name = "reason")
    val reason: String? = reason
}
