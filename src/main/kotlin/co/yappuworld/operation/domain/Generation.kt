package co.yappuworld.operation.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate

@Table("generations")
class Generation(
    value: Int,
    startDate: LocalDate,
    endDate: LocalDate,
    isActive: Boolean
) : Persistable<Int> {

    @Id
    private var value: Int = value

    var startDate = startDate
        private set
    var endDate = endDate
        private set
    var isActive = false
        private set

    @Transient
    private var isNew = true

    override fun getId(): Int = this.value

    override fun isNew(): Boolean = isNew

    fun load() {
        this.isNew = false
    }
}
