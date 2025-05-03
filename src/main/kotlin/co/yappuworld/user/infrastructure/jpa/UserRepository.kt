package co.yappuworld.user.infrastructure.jpa

import co.yappuworld.user.infrastructure.entity.UserEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository :
    JpaRepository<UserEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun existsUserByEmail(email: String): Boolean

    fun findUserOrNullByEmail(email: String): UserEntity?

    fun findAllByIdIn(userIds: List<UUID>): List<UserEntity>
}
