package day09

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { line ->
                val numbers = line.split(" ").map { it.toInt() }
                val lines = mutableListOf<List<Int>>()
                lines.add(numbers)
                while (!lines.last().all { it == 0 }) {
                    val currentLine = lines.last()
                    lines.add((0 until currentLine.size - 1).map { currentLine[it + 1] - currentLine[it] })
                }
                lines.reversed().drop(1).map { it.last() }.fold(0) { acc, number -> acc + number }
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return input
            .map { line ->
                val numbers = line.split(" ").map { it.toInt() }
                val lines = mutableListOf<List<Int>>()
                lines.add(numbers)
                while (!lines.last().all { it == 0 }) {
                    val currentLine = lines.last()
                    lines.add((0 until currentLine.size - 1).map { currentLine[it + 1] - currentLine[it] })
                }
                lines.reversed().drop(1).map { it.first() }.fold(0) { acc, number -> number - acc }
            }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(9, isTest = true)
    check(part1(testInput) == 114)
    check(part2(testInput) == 2)

    val input = readInput(9)
    part1(input).println()
    part2(input).println()
}
