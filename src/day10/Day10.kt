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
        val map = input.map { it.toCharArray().toList() }
        val startPosition = getStartPosition(map)
        val mover = getConnectingPositions(map, startPosition)
            .map { Mover(it, listOf(startPosition)) }
            .mapNotNull { it.moveUntilClosedLoop(map) }
            .minBy { it.pastPositions.size }

        val MARKER_INSIDE = 'I'
        val MARKER_OUTSIDE = 'O'
        val MARKER_PIPE = '█'
        val highResPipes = mapOf(
            '.' to listOf(
                "...".toCharArray(),
                "...".toCharArray(),
                "...".toCharArray(),
            ),
            'S' to listOf(
                "███".toCharArray(),
                "███".toCharArray(),
                "███".toCharArray(),
            ),
            'F' to listOf(
                "...".toCharArray(),
                ".██".toCharArray(),
                ".█.".toCharArray(),
            ),
            '|' to listOf(
                ".█.".toCharArray(),
                ".█.".toCharArray(),
                ".█.".toCharArray(),
            ),
            'L' to listOf(
                ".█.".toCharArray(),
                ".██".toCharArray(),
                "...".toCharArray(),
            ),
            '-' to listOf(
                "...".toCharArray(),
                "███".toCharArray(),
                "...".toCharArray(),
            ),
            'J' to listOf(
                ".█.".toCharArray(),
                "██.".toCharArray(),
                "...".toCharArray(),
            ),
            '7' to listOf(
                "...".toCharArray(),
                "██.".toCharArray(),
                ".█.".toCharArray(),
            ),
            MARKER_INSIDE to listOf(
                "III".toCharArray(),
                "III".toCharArray(),
                "III".toCharArray(),
            ),
            MARKER_OUTSIDE to listOf(
                "OOO".toCharArray(),
                "OOO".toCharArray(),
                "OOO".toCharArray(),
            ),
        )

        val detailedMap = Array(map.size * 3) { y -> Array(map[0].size * 3) { x -> '.'} }
        fun fillDetailedMap(smallX: Int, smallY: Int, fillChar: Char) {
            val bigX = smallX * 3
            val bigY = smallY * 3
            for (x in bigX until bigX + 3) {
                for (y in bigY until bigY + 3) {
                    val smallResXMod = x % 3
                    val smallResYMod = y % 3
                    val highResPipe = highResPipes[fillChar]
                    detailedMap[y][x] = highResPipe!![smallResYMod][smallResXMod]
                }
            }
        }

        fun positionIsPipe(smallPosition: Position): Boolean {
            val bigX = smallPosition.x * 3
            val bigY = smallPosition.y * 3
            for (x in bigX until bigX + 3) {
                for (y in bigY until bigY + 3) {
                    if (detailedMap[y][x] == MARKER_PIPE) {
                        return true
                    }
                }
            }
            return false
        }

        fun isOutsideOfDetailedMap(position: Position) =
            position.x < 0 || position.y < 0 || position.x > detailedMap[0].size - 1 || position.y > detailedMap.size - 1

        fun findWayOut(position: Position): List<Position> {
            val visitedPositions = mutableSetOf(position)
            var traces = listOf(listOf(position))
            while (true) {
                if (traces.isEmpty()) return emptyList()

                traces = traces.flatMap {trace ->
                    val currentPosition = trace.last()
                    val newPositions = listOf(
                        Position(currentPosition.x - 1, currentPosition.y),
                        Position(currentPosition.x + 1, currentPosition.y),
                        Position(currentPosition.x, currentPosition.y - 1),
                        Position(currentPosition.x, currentPosition.y + 1),
                    )
                        .filter { !visitedPositions.contains(it) }
                        .filter { isOutsideOfDetailedMap(it) || detailedMap[it.y][it.x] != MARKER_PIPE }
                    if (newPositions.any { isOutsideOfDetailedMap(it) || detailedMap[it.y][it.x] == MARKER_OUTSIDE }) {
                        return trace
                    }
                    visitedPositions.addAll(newPositions)
                    newPositions.map { trace + it }
                }
            }
        }

        mover.pastPositions.forEach { position ->
            val (x, y) = position
            val fillChar = map[y][x]
            fillDetailedMap(x, y, fillChar)
        }

        val insidePositions = mutableListOf<Position>()
        for (smallY in map.indices) {
            for(smallX in map[smallY].indices) {
                if (positionIsPipe(Position(smallX, smallY))) {
                    continue
                }
                val wayOutTrace = findWayOut(Position(smallX * 3, smallY * 3))
                if (wayOutTrace.isNotEmpty()) {
                    fillDetailedMap(smallX, smallY, MARKER_OUTSIDE)
                    wayOutTrace.forEach { detailedMap[it.y][it.x] = MARKER_OUTSIDE }
                } else {
                    fillDetailedMap(smallX, smallY, MARKER_INSIDE)
                    insidePositions.add(Position(smallX, smallY))
                }
            }
        }

        return insidePositions.size
    }

    // test if implementation meets criteria from the description, like:
    check(part1(readInput(10, isTest = true)) == 4)
    check(part2(readInput(10, part = 2, isTest = true)) == 8)

    val input = readInput(10)
    part1(input).println()
    part2(input).println()
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
