package co.yappuworld.global.util

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

object PageUtils {

    fun <T : Any> Page<T?>.filterNotNull(): Page<T> =
        PageImpl(
            this.content.filterNotNull(),
            this.pageable,
            this.totalElements
        )
}
