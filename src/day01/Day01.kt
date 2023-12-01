package day01

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.filter { c->c.isDigit() } }
            .map { it.first().digitToInt() * 10 + it.last().digitToInt() }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(1, isTest = true)
    check(part1(testInput) == 142)

    val input = readInput(1)
    part1(input).println()
}
