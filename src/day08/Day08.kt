package day08

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val inputParsed = parseInput(input)

        var currentNode = "AAA"
        var currentStep = 0

        while (currentNode != "ZZZ") {
            val instruction = inputParsed.instructions[currentStep % inputParsed.instructions.size]
            val (left, right) = inputParsed.network[currentNode]!!
            println("$currentStep: $currentNode -> $instruction ($left, $right)")
            currentNode = if (instruction == Input.RIGHT) { right } else { left }
            currentStep++
        }

        return currentStep
    }

    fun part2(input: List<String>): Long {
        val inputParsed = parseInput(input)

        var ghostsCurrentNode = inputParsed.network.keys.filter { it.endsWith("A") }
        var currentStep = 0L

        while (!ghostsCurrentNode.all { it.endsWith("Z") }) {
            ghostsCurrentNode = ghostsCurrentNode.map {currentNode ->
                val instruction = inputParsed.instructions[(currentStep % inputParsed.instructions.size).toInt()]
                val (left, right) = inputParsed.network[currentNode]!!
                if (instruction == Input.RIGHT) { right } else { left }
            }
            if (currentStep % 10000000L == 0L) currentStep.println()
            currentStep++
        }

        return currentStep
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(8, isTest = true)
    check(part1(testInput) == 6)
    check(part2(readInput(8, part = 2, isTest = true)) == 6L)

    val input = readInput(8)
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): Input {
    val instructions = input[0].toList()
    val network = input.drop(2)
        .map {
            val (current, left, right) = it
                .replace(Regex("[() ]"), "").replace("=", ",")
                .split(",")
            current to Pair(left, right)
        }
        .associate { it }
    return Input(instructions, network)
}

data class Input(
    val instructions: List<Char>,
    val network: Map<String, Pair<String, String>>
) {
    companion object {
        const val RIGHT = 'R'
        const val LEFT = 'L'
    }
}
