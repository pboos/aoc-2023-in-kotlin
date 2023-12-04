package day03

import println
import readInput

fun main() {
    val numberFinder = Regex("\\d+")
    fun part1(input: List<String>): Int {
        val symbolRegex = Regex("[^0-9.]")

        fun hasSymbolAt(rowNumber: Int, columnRange: IntRange): Boolean {
            val isStartLine = rowNumber == 0
            val isEndLine = rowNumber == input.size - 1

            // above
            if (!isStartLine) {
                val line = input[rowNumber - 1]
                if (line.substring(columnRange).contains(symbolRegex)) return true
            }
            // below
            if (!isEndLine) {
                val line = input[rowNumber + 1]
                if (line.substring(columnRange).contains(symbolRegex)) return true
            }

            fun getColumn(column: Int): String {
                if (column < 0 || column > input[rowNumber].length - 1) return ""
                return "${input[rowNumber][column]}${if (rowNumber > 0) input[rowNumber - 1][column] else ""}${if (rowNumber < input.size - 1) input[rowNumber + 1][column] else ""}"
            }

            val leftColumn = getColumn(columnRange.first - 1)
            if (leftColumn.contains(symbolRegex)) return true
            val rightColumn = getColumn(columnRange.last + 1)
            if (rightColumn.contains(symbolRegex)) return true

            return false
        }

        return input
            .mapIndexed { index, line ->
                numberFinder.findAll(line)
                    .filter { hasSymbolAt(index, it.range) }
                    .map { it.value.toInt() }
                    .sum()
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        val lineLength = input[0].length
        // TODO create matrix of numbers (object) and *
        val matrix = input
            .mapIndexed { index, line ->
                val lineEntries = Array<MatrixEntry>(line.length) { MatrixEntry.Empty }
                numberFinder.findAll(line)
                    .map { MatrixEntry.Number(value = it.value.toInt(), rowNumber = index, columnRange = it.range) }
                    .forEach { number ->
                        for (column in number.columnRange) {
                            lineEntries[column] = number
                        }
                    }
                Regex("\\*").findAll(line).forEach { lineEntries[it.range.first] = MatrixEntry.Star }
                lineEntries
            }
            .toList()

        fun findGearNumbers(rowIndex: Int, columnIndex: Int): List<MatrixEntry.Number> {
            val gearNumbers = mutableListOf<MatrixEntry.Number>()

            val startColumn = 0.coerceAtLeast(columnIndex - 1)
            val endColumn = (columnIndex + 1).coerceAtMost(lineLength - 1)
            if (rowIndex > 0) {
                gearNumbers.addAll(
                    matrix[rowIndex - 1].slice(startColumn..endColumn).filterIsInstance<MatrixEntry.Number>()
                )
            }
            if (rowIndex < matrix.size - 1) {
                gearNumbers.addAll(
                    matrix[rowIndex + 1].slice(startColumn..endColumn).filterIsInstance<MatrixEntry.Number>()
                )
            }
            if (columnIndex > 0) {
                val entry = matrix[rowIndex][columnIndex - 1]
                if (entry is MatrixEntry.Number) gearNumbers.add(entry)
            }
            if (columnIndex < lineLength - 1) {
                val entry = matrix[rowIndex][columnIndex + 1]
                if (entry is MatrixEntry.Number) gearNumbers.add(entry)
            }

            return gearNumbers.distinct()
        }

        return matrix
            .mapIndexed { rowIndex, line ->
                line
                    .mapIndexed { columnIndex, column ->
                        if (column !is MatrixEntry.Star) 0
                        else {
                            val gearNumbers = findGearNumbers(rowIndex = rowIndex, columnIndex = columnIndex)
                            if (gearNumbers.size != 2) 0
                            else gearNumbers[0].value * gearNumbers[1].value
                        }
                    }
                    .sum()
            }
            .sum()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(3, isTest = true)
    check(part1(testInput) == 4361)
    check(part2(testInput) == 467835)

    val input = readInput(3)
    part1(input).println()
    part2(input).println()
}

sealed class MatrixEntry {
    data object Empty : MatrixEntry()
    data object Star : MatrixEntry()
    data class Number(
        val value: Int,
        val rowNumber: Int,
        val columnRange: IntRange
    ) : MatrixEntry()
}
