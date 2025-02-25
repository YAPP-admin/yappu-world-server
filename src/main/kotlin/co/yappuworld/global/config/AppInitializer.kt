package co.yappuworld.global.config

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.util.TimeZone

@Component
class AppInitializer {

    @PostConstruct
    fun setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"))
    }
}
