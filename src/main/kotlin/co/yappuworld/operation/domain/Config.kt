package co.yappuworld.operation.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("config")
class Config(
    @Id
    val id: String,
    val label: String,
    val category: ConfigCategory,
    value: String?
) {
    var value: String? = value
        private set

    fun updateValue(value: String?) {
        this.value = value
    }
}
