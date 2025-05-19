package co.yappuworld.user.infrastructure.jpa

import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository :
    JpaRepository<UserEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun existsUserByEmail(email: String): Boolean

    fun findUserOrNullByEmail(email: String): UserEntity?

    fun findAllByIdIn(userIds: List<UUID>): List<UserEntity>

    @Query(value = "select get_lock(:email, :timeoutSeconds)", nativeQuery = true)
    fun getLock(
        @Param("email") email: String,
        @Param("timeoutSeconds") timeoutSeconds: Long = 3L
    ): Int?

    @Query(value = "select release_lock(:email)", nativeQuery = true)
    fun releaseLock(
        @Param("email") email: String
    ): Int?
}
