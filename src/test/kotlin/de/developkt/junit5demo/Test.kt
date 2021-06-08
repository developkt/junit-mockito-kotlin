package de.developkt.junit5demo

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class Test {

    @BeforeEach
    fun runBefore() {
        println("before test")
    }

    @BeforeAll
    fun runBeforeAll() {
        println("run once before all tests")
    }

    @Test
    fun firstTest() {
        println("hello world")
    }

    @Test
    @Disabled
    fun secondTest() {
        println("hello world from second test")
    }

    @AfterEach
    fun runAfter() {
        println("after test")
    }

    @AfterAll
    fun runAfterAll() {
        println("run once after all tests")
    }
}