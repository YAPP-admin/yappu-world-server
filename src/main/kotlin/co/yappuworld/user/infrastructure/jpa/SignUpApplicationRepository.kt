package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.user.infrastructure.entity.SignUpApplicationEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface SignUpApplicationRepository :
    JpaRepository<SignUpApplicationEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun findAllByIdIn(ids: List<UUID>): List<SignUpApplicationEntity>
}
