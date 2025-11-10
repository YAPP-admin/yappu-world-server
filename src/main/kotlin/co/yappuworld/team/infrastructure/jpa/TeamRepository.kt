package co.yappuworld.team.infrastructure.jpa

import co.yappuworld.team.infrastructure.entity.TeamEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface TeamRepository :
    JpaRepository<TeamEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun existsTeamByName(name: String): Boolean

    fun existsByGenerationAndName(
        generation: Int,
        name: String
    ): Boolean

    fun existsByGenerationAndNameAndIdNot(
        generation: Int,
        name: String,
        id: UUID
    ): Boolean
}
