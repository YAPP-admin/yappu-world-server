package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.User
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : CrudRepository<User, UUID> {

    fun existsUserByEmail(email: String): Boolean

    fun findUserOrNullByEmail(email: String): User?

    @Query(
        """
            SELECT u.*, ra.*, u.id user_id, ra.id activity_unit_id 
            FROM users u
            INNER JOIN (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY generation DESC) AS rn
                FROM activity_units
            ) ra ON u.id = ra.user_id AND ra.rn = 1
            ORDER BY u.id ASC
            LIMIT :limit OFFSET :offset;
        """
    )
    fun findUsersWithActivityUnit(
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<UserWithLastActivityUnit>
}
