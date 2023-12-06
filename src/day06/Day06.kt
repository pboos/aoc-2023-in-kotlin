package day06

import println
import readInput

fun main() {
    fun getNumberOfPossibleWaysToWin(race: Race): Long =
        (0..race.time).filter { speed -> (race.time - speed) * speed > race.recordDistance }.size.toLong()

    fun part1(input: List<String>): Long {
        fun parse(input: List<String>): List<Race> {
            val times = input[0].split(" ").mapNotNull { it.toLongOrNull() }
            val recordDistances = input[1].split(" ").mapNotNull { it.toLongOrNull() }
            return times.mapIndexed { index, time -> Race(time = time, recordDistance = recordDistances[index]) }
        }

        val races = parse(input)

        return races.map { race -> getNumberOfPossibleWaysToWin(race) }.fold(1) { acc, number -> acc * number }
    }

    fun part2(input: List<String>): Long {
        fun parse(input: List<String>): Race {
            val time = input[0].split(":").last().replace(" ", "").toLong()
            val recordDistance = input[1].split(":").last().replace(" ", "").toLong()
            return Race(time = time, recordDistance = recordDistance)
        }

        val race = parse(input)
        return getNumberOfPossibleWaysToWin(race)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(6, isTest = true)
    check(part1(testInput) == 288L)
    check(part2(testInput) == 71503L)

    val input = readInput(6)
    part1(input).println()
    part2(input).println()
}

data class Race(
    val time: Long,
    val recordDistance: Long,
)
