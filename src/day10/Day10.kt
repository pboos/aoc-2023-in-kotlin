package day10

import println
import readInput
import kotlin.math.ceil

fun main() {
    fun part1(input: List<String>): Int {
        val map = input.map { it.toCharArray().toList() }
        val startPosition = getStartPosition(map)

        val movers = getConnectingPositions(map, startPosition).map { Mover(it, listOf(startPosition)) }
        val finishedMovers = movers.mapNotNull { it.moveUntilClosedLoop(map) }
        return finishedMovers.map { ceil(it.pastPositions.size / 2.0).toInt() }.min()
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(10, isTest = true)
    check(part1(testInput) == 4)
    check(part1(readInput(10, part = 2, isTest = true)) == 8)
//    check(part2(testInput) == 2)

    val input = readInput(10)
    part1(input).println()
//    part2(input).println()
}

data class Position(val x: Int, val y: Int)
data class Mover(val position: Position, val pastPositions: List<Position>) {
    fun move(map: List<List<Char>>): Mover {
        val connectingPositions = getConnectingPositions(map, position).filter { it != pastPositions.last() }
        if (connectingPositions.isEmpty()) {
            throw Exception("No connecting positions found")
        }
        assert(connectingPositions.size == 1)
        return Mover(connectingPositions.first(), pastPositions + position)
    }

    fun moveUntilClosedLoop(map: List<List<Char>>): Mover? {
        var currentMover = this
        while (true) {
            if (currentMover.position == pastPositions.first()) {
                return currentMover
            }

            try {
                currentMover = currentMover.move(map)
            } catch (e: Exception) {
                return null
            }
        }
    }
}

fun getStartPosition(map: List<List<Char>>): Position {
    for (y in map.indices) {
        for (x in map[y].indices) {
            if (map[y][x] == 'S') {
                return Position(x, y)
            }
        }
    }
    error("No start position found")
}

fun getConnectingPositions(map: List<List<Char>>, position: Position): List<Position> {
    val currentPipe = map[position.y][position.x]
    val westOpeningPipes = listOf('S', '-', 'J', '7')
    val eastOpeningPipes = listOf('S', '-', 'L', 'F')
    val northOpeningPipes = listOf('S', '|', 'L', 'J')
    val southOpeningPipes = listOf('S', '|', 'F', '7')

    return listOfNotNull(
        if (westOpeningPipes.contains(currentPipe)) {
            Pair(Position(position.x - 1, position.y), eastOpeningPipes)
        } else null,
        if (eastOpeningPipes.contains(currentPipe)) {
            Pair(Position(position.x + 1, position.y), westOpeningPipes)
        } else null,
        if (northOpeningPipes.contains(currentPipe)) {
            Pair(Position(position.x, position.y - 1), southOpeningPipes)
        } else null,
        if (southOpeningPipes.contains(currentPipe)) {
            Pair(Position(position.x, position.y + 1), northOpeningPipes)
        } else null,
    )
        .filter {
            val (pos, chars) = it
            val (x, y) = pos
            x >= 0 && y >= 0 && y < map.size && x < map[y].size && chars.contains(map[y][x])
        }
        .map { it.first }
}
