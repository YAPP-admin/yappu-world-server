package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    fun existsUserByEmail(email: String): Boolean

    fun findUserOrNullByEmail(email: String): User?
}
