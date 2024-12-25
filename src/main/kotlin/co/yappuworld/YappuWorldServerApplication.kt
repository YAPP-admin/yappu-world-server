package co.yappuworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class YappuWorldServerApplication

fun main(args: Array<String>) {
    runApplication<YappuWorldServerApplication>(*args)
}
