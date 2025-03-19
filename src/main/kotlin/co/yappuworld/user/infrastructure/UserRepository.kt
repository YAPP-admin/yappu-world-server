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

    fun findAllByIdIn(userIds: List<UUID>): List<User>

    @Query(
        """
            SELECT 
                u.id AS user_id, 
                u.email, 
                u.name, 
                u.role, 
                u.is_active, 
                u.created_at, 
                la.generation, 
                la.position, 
                la.id AS activity_unit_id
            FROM users u
            INNER JOIN (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY generation DESC) AS rn
                FROM activity_units
            ) la ON u.id = la.user_id AND la.rn = 1
            ORDER BY u.id ASC
            LIMIT :limit OFFSET :offset;
        """
    )
    fun findUsersWithActivityUnit(
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): List<UserWithLastActivityUnit>

    @Query(
        """
            SELECT 
                u.id AS user_id, 
                u.email, 
                u.name, 
                u.role, 
                u.is_active, 
                u.created_at, 
                la.generation, 
                la.position, 
                la.id AS activity_unit_id
            FROM users u
            INNER JOIN (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY generation DESC) AS rn
                FROM activity_units
            ) la ON u.id = la.user_id AND la.rn = 1
            WHERE u.id in (:#{#userIds});
        """
    )
    fun findUsersWithActivityUnit(
        @Param("userIds") userIds: Collection<String>
    ): List<UserWithLastActivityUnit>

    @Query(
        """
            SELECT 
                u.id AS user_id, 
                u.email, 
                u.name, 
                u.role, 
                u.is_active, 
                u.created_at, 
                la.generation, 
                la.position, 
                la.id AS activity_unit_id
            FROM users u
            INNER JOIN (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY generation DESC) AS rn
                FROM activity_units
            ) la ON u.id = la.user_id AND la.rn = 1
            WHERE u.id = :userId;
        """
    )
    fun findUserWithActivityUnit(
        @Param("userId") userId: String
    ): UserWithLastActivityUnit
}
