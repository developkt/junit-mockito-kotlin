package de.developkt.junit5demo

import java.lang.IllegalArgumentException

class Calculator {

    fun positiveSum(first: Int, second: Int) : Int {
        if (first >= 0 && second >= 0) return first + second else
            throw IllegalArgumentException("At least one parameter is not an positive integer")
    }
}