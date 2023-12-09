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
            currentNode = if (instruction == Input.RIGHT) right else left
            currentStep++
        }

        return currentStep
    }

    fun part2(input: List<String>): ULong {
        val inputParsed = parseInput(input)

        val nodesWithInfo = inputParsed.network
            .map {
                val name = it.key
                val endNodeSteps = mutableListOf<Int>()
                var currentStepName = name
                for (i in inputParsed.instructions.indices) {
                    val instruction = inputParsed.instructions[i]
                    val (left, right) = inputParsed.network[currentStepName]!!
                    currentStepName = if (instruction == Input.RIGHT) right else left
                    if (currentStepName.endsWith("Z")) {
                        endNodeSteps.add(i + 1)
                    }
                }
                name to NetworkNodeInfo(name = name, next = currentStepName, endNodeSteps = endNodeSteps)
            }
            .associate { it }

        var ghostsCurrentNode = inputParsed.network.keys.filter { it.endsWith("A") }
        var currentStep: ULong = 0.toULong()

        while (true) {
            val ghostsCurrentNodeInfo = ghostsCurrentNode.map { nodesWithInfo[it]!! }
            val possibleEndSteps = ghostsCurrentNodeInfo
                .map { it.endNodeSteps.map { steps -> steps.toULong() + currentStep } }
                .reduce { acc, list -> acc.intersect(list.toSet()).toList().sorted() }
            if (possibleEndSteps.isNotEmpty()) {
                return possibleEndSteps.first()
            }
            ghostsCurrentNode = ghostsCurrentNodeInfo.map { it.next }.distinct()
            currentStep += inputParsed.instructions.size.toULong()

            if (currentStep % 1000000.toULong() == 0.toULong()) {
                println("currentStep (${ghostsCurrentNode.size}): $currentStep")
                println(ghostsCurrentNode)
            }
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(8, isTest = true)
    check(part1(testInput) == 6)
    check(part2(readInput(8, part = 2, isTest = true)) == 6.toULong())

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
    }
}

data class NetworkNodeInfo(
    val name: String,
    val next: String,
    val endNodeSteps: List<Int>,
)
