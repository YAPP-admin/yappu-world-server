package co.yappuworld.operation.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("config")
class Config(
    @Id
    val id: String,
    label: String,
    val category: ConfigCategory,
    value: String?
) {
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
}
