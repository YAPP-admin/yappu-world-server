package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.PostEntity
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PostRepository :
    JpaRepository<PostEntity, UUID>,
    KotlinJdslJpqlExecutor {

    fun findAllByIdIn(ids: List<UUID>): List<PostEntity>
}
