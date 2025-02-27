package co.yappuworld.operation.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("config")
class Config(
    @Id
    val id: String,
    val label: String,
    val category: ConfigCategory,
    val value: String?
)
