package co.yappuworld.operation.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity(name = "generations")
class GenerationEntity(
    @Id
    val value: Int,
    startDate: LocalDate?,
    endDate: LocalDate?
) {

    var startDate = startDate
        private set
    var endDate = endDate
        private set
    var isActive = false
        private set

    fun activate() {
        this.isActive = true
    }

    fun deactivate() {
        this.isActive = false
    }
}
