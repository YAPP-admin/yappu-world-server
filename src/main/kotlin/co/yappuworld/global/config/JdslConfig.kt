package co.yappuworld.global.config

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JdslConfig {

    @Bean
    fun jpqlRenderContext() = JpqlRenderContext()
}
