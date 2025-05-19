package co.yappuworld.user.infrastructure.jpa

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ActivityUnitRepository :
    JpaRepository<ActivityUnitEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun findAllByUserId(userId: UUID): List<ActivityUnitEntity>
}
