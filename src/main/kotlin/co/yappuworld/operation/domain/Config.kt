package co.yappuworld.operation.domain

import org.springframework.data.relational.core.mapping.Table

@Table("config")
class Config(
    val id: String,
    val value: String
)
