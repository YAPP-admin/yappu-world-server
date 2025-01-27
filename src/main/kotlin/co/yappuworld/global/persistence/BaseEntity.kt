package co.yappuworld.global.persistence

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

abstract class BaseEntity {

    @CreatedDate
    lateinit var createdAt: LocalDateTime

    @LastModifiedDate
    lateinit var updatedAt: LocalDateTime

    fun isCreatedAtInitialized(): Boolean {
        return ::createdAt.isInitialized
    }
}
