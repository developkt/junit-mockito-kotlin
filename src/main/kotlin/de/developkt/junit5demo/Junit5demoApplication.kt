package de.developkt.junit5demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Junit5demoApplication

fun main(args: Array<String>) {
	runApplication<Junit5demoApplication>(*args)
}
