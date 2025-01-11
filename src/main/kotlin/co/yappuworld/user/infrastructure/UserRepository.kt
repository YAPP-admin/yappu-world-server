package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID>
