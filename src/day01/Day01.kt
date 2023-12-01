package day01

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { it.filter { c -> c.isDigit() } }
            .map { it.first().digitToInt() * 10 + it.last().digitToInt() }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val inputWithReplacedSpelledNumbers = input
            .map {
                it.replace("one", "o1e", ignoreCase = true)
                    .replace("two", "t2o", ignoreCase = true)
                    .replace("three", "th3ree", ignoreCase = true)
                    .replace("four", "fo4ur", ignoreCase = true)
                    .replace("five", "fi5ve", ignoreCase = true)
                    .replace("six", "si6x", ignoreCase = true)
                    .replace("seven", "se7ven", ignoreCase = true)
                    .replace("eight", "ei8ght", ignoreCase = true)
                    .replace("nine", "ni9ne", ignoreCase = true)
            }
        inputWithReplacedSpelledNumbers.println()
        return part1(inputWithReplacedSpelledNumbers)
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput(1, isTest = true)) == 142)
    check(part2(readInput(1, part = 2, isTest = true)) == 281)

    val input = readInput(1)
    part1(input).println()
    part2(input).println()
}
