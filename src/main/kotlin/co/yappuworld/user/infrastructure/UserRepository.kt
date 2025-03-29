package co.yappuworld.user.infrastructure

import co.yappuworld.user.domain.model.UserEntity
import co.yappuworld.user.infrastructure.model.UserWithLastActivityUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserRepository : JpaRepository<UserEntity, UUID> {

    fun existsUserByEmail(email: String): Boolean

    fun findUserOrNullByEmail(email: String): UserEntity?

    fun findAllByIdIn(userIds: List<UUID>): List<UserEntity>

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
        """,
        nativeQuery = true
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
        """,
        nativeQuery = true
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
            WHERE u.id = :userId
        """,
        nativeQuery = true
    )
    fun findUserWithActivityUnit(
        @Param("userId") userId: String
    ): UserWithLastActivityUnit
}
