package co.yappuworld.operation.domain

import co.yappuworld.global.persistence.BaseJpaEntity
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.PostLoad
import jakarta.persistence.PostPersist
import jakarta.persistence.Table
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.domain.Persistable
import java.io.Serializable
import java.util.Objects

// TODO: alert id field to name
@Entity
@Table(name = "config")
class ConfigEntity(
    @Id
    val name: String,
    label: String,
    val category: ConfigCategory,
    value: String?
) : Persistable<String> {

    var label: String = label
        private set

    var value: String? = value
        private set

    fun update(value: String?) {
        this.value = value
    }

    fun update(
        label: String,
        value: String?
    ) {
        this.label = label
        this.value = value
    }

    @Transient
    private var _isNew = true

    override fun getId(): String = this.name

    override fun isNew(): Boolean = _isNew

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (other !is HibernateProxy && this::class != other::class) {
            return false
        }

        return name == getIdentifier(other)
    }

    private fun getIdentifier(obj: Any): Serializable =
        if (obj is HibernateProxy) {
            obj.hibernateLazyInitializer.identifier as Serializable
        } else {
            (obj as BaseJpaEntity).id
        }

    override fun hashCode() = Objects.hashCode(name)

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}
