package co.yappuworld.operation.domain

import co.yappuworld.global.persistence.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.time.LocalDate
import java.util.Objects

@Entity
@Table(name = "generations")
class GenerationEntity(
    @Id
    val value: Int,
    startDate: LocalDate?,
    endDate: LocalDate?
) : Persistable<Int> {

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

    @Transient
    private var _isNew = true

    override fun getId(): Int = this.value

    override fun isNew(): Boolean = _isNew

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is HibernateProxy && this::class != other::class) {
            return false
        }

        return id == getIdentifier(other)
    }

    private fun getIdentifier(obj: Any): Serializable =
        if (obj is HibernateProxy) {
            obj.hibernateLazyInitializer.identifier as Serializable
        } else {
            (obj as BaseEntity).id
        }

    override fun hashCode() = Objects.hashCode(id)

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}
