package day02

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val games = parseInput(input)

        // possible games for: 12 red, 13 green, 14 blue
        val possibleGames = games.filter {
            val maxRed = it.sets.maxOf { it.redCount }
            val maxGreen = it.sets.maxOf { it.greenCount }
            val maxBlue = it.sets.maxOf { it.blueCount }
            maxRed <= 12 && maxGreen <= 13 && maxBlue <= 14
        }

        return possibleGames.sumOf { it.number }
    }

    fun part2(input: List<String>): Int {
        val games = parseInput(input)

        val fewestStonesPerGame = games.associateByTo(
            mutableMapOf(),
            { it.number },
            { game ->
                val maxRed = game.sets.maxOf { it.redCount }
                val maxGreen = game.sets.maxOf { it.greenCount }
                val maxBlue = game.sets.maxOf { it.blueCount }
                GameSet(redCount = maxRed, greenCount = maxGreen, blueCount = maxBlue)
            }
        )

        return fewestStonesPerGame.values.sumOf { it.redCount * it.greenCount * it.blueCount }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(2, isTest = true)
    check(part1(testInput) == 8)
    check(part2(testInput) == 2286)

    val input = readInput(2)
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): List<Game> {
    return input.map { line ->
        val colonIndex = line.indexOf(':')
        val gameNumber = line.substring(5, colonIndex).toInt()

        val sets = line.substring(colonIndex + 2)
            .split(";")
            .map { set ->
                val revealedItems = set.split(",")
                    .map {
                        // " 3 green"
                        val (count, color) = it.trim().split(" ")
                        count.toInt() to color
                    }
                    .associateByTo(mutableMapOf(), { it.second }, { it.first })
                GameSet(
                    redCount = revealedItems.getOrDefault("red", 0),
                    greenCount = revealedItems.getOrDefault("green", 0),
                    blueCount = revealedItems.getOrDefault("blue", 0),
                )
            }

        Game(
            number = gameNumber,
            sets = sets,
        )
    }
}

data class Game(
    val number: Int,
    val sets: List<GameSet>,
)

data class GameSet(
    val redCount: Int,
    val greenCount: Int,
    val blueCount: Int,
)
