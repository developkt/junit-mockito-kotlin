package de.developkt.junit5demo

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import java.util.*
import kotlin.random.Random

class CalculatorTest {

    private lateinit var calculator: Calculator

    @BeforeEach
    fun setUp() {
        calculator = Calculator()
    }

    @Nested
    @DisplayName("all success cases for calculator class")
    inner class SuccessCases {

        @Test
        fun `should return a positive sum given two positive integers`() {
            val sum = calculator.positiveSum(3, 5)
            assertAll(
                { assertTrue(sum > 0) },
                { assertEquals(8, sum) },
                { assertFalse(sum <= 0) },
                { assertNotNull(sum) }
            )
        }

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource(
            "1, 5, 6",
            "7, 3, 10"
        )
        fun `should calculate a valid sum of two integers`(first: Int, second: Int, result: Int) {
            val sum = calculator.positiveSum(first, second)
            assertEquals(result, sum)
        }


        @ParameterizedTest
        @ValueSource(ints = [1, 5, 55, 98])
        fun `should add input value to 0 and return input value`(input: Int) {
            val sum = calculator.positiveSum(0, input)
            assertEquals(input, sum)
        }

        @Test
        fun `should only run on our CI system`() {
            assumeTrue(System.getenv("CI") == "true")
            assertEquals(5, calculator.positiveSum(2, 3))
        }

        @TestFactory
        fun `test factory for x+100 tests`(): List<DynamicNode> {
            return List(10) { Random.nextInt(0, 100) }
                .map {
                    dynamicTest("should add $it to 100 and return ${it + 100}") {
                        assertEquals(100 + it, calculator.positiveSum(it, 100))
                    }
                }
        }

    }

    @Nested
    @DisplayName("all failure cases for calculator class")
    inner class FailureCases {

        @Test
        fun `should throw an exception if one argument is lower then 0`() {
            assertThrows(IllegalArgumentException::class.java) {
                calculator.positiveSum(-4, 5)
            }
        }

    }

}
