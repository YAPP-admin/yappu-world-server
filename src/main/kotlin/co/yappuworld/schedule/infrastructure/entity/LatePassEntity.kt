package co.yappuworld.schedule.infrastructure.entity

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "late_passes")
class LatePassEntity(
    val userId: UUID,
    val generation: Int
) : BaseEntity() {

    @Column(name = "count", nullable = false)
    var count: Int = 0
        private set

    fun updateCount(latePassCount: Int) {
        this.count = latePassCount
    }
}
