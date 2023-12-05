package day04

import println
import readInput
import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val colonIndex = line.indexOf(":")
            val (winningNumbers, numbers) = line.substring(colonIndex + 2).split("|")
                .map { numbers ->
                    numbers.trim().split(" ")
                        .filter { number -> number.isNotBlank() }
                        .map { number -> number.trim().toInt() }
                }

            val numbersWeHaveThatAreWinningNumbers = numbers.intersect(winningNumbers.toSet()).size

            if (numbersWeHaveThatAreWinningNumbers == 0) {
                0
            } else {
                1 * 2.0.pow(numbersWeHaveThatAreWinningNumbers - 1.0).toInt()
            }
        }
    }

    fun part2(input: List<String>): Int {
        val numberOfEachCard = Array(input.size) { 1 }

        input.forEachIndexed { index, line ->
            val colonIndex = line.indexOf(":")
            val (winningNumbers, numbers) = line.substring(colonIndex + 2).split("|")
                .map { numbers ->
                    numbers.trim().split(" ")
                        .filter { number -> number.isNotBlank() }
                        .map { number -> number.trim().toInt() }
                }

            val numbersWeHaveThatAreWinningNumbers = numbers.intersect(winningNumbers.toSet()).size

            val howManyCards = numberOfEachCard[index]
            for (i in index + 1 .. (index + numbersWeHaveThatAreWinningNumbers).coerceAtMost(input.size - 1)) {
                numberOfEachCard[i] += howManyCards
            }
        }

        return numberOfEachCard.sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(4, isTest = true)
    check(part1(testInput) == 13)
    check(part2(testInput) == 30)

    val input = readInput(4)
    part1(input).println()
    part2(input).println()
}
