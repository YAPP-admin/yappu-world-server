package co.yappuworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
class YappuWorldServerApplication

fun main(args: Array<String>) {
    runApplication<YappuWorldServerApplication>(*args)
}
