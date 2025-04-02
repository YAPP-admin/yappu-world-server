package co.yappuworld.post.infrastructure

import co.yappuworld.post.domain.PostEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PostCommandService(
    private val postRepository: PostRepository
) {

    fun save(post: PostEntity): PostEntity = postRepository.save(post)

    fun deleteAll(posts: List<PostEntity>) {
        postRepository.deleteAll(posts)
    }
}
