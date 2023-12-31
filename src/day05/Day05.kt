package day05

import println
import readInput

fun main() {
    fun part1(input: List<String>): Long {
        val parsedInput = parseInput(input)
        return parsedInput.seeds.map {
            parsedInput.mappers.fold(it) { acc, mapper -> mapper.map(acc) }
        }.min()
    }

    fun part2(input: List<String>): Long {
        val parsedInput = parseInput(input)
        val seedRanges = mutableListOf<LongRange>()
        for (i in parsedInput.seeds.indices step 2) {
            val rangeStart = parsedInput.seeds[i]
            seedRanges.add(rangeStart until rangeStart + parsedInput.seeds[i + 1])
        }

        return seedRanges
            .map { seedRange ->
                parsedInput.mappers
                    .fold(listOf(seedRange)) { acc, mapper ->
                        acc.map { range -> mapper.mapRange(range) }.flatten()
                    }
                    .minOf { range -> range.first() }
            }
            .min()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(5, isTest = true)
    check(part1(testInput) == 35L)
    check(part2(testInput) == 46L)

    val input = readInput(5)
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): Input {
    val seeds = input.first().substring(7).split(" ").map { it.toLong() }

    val mappers = mutableListOf<Mapper>()
    var currentMapper: Mapper? = null
    var currentMapperRanges = mutableListOf<MapperRange>()
    input.subList(1, input.size).forEach { line ->
        if (line.isBlank()) {
            // new mapper on next line
            currentMapper?.let {
                mappers.add(it.copy(ranges = currentMapperRanges))
            }

            currentMapper = null
            currentMapperRanges = mutableListOf()
            return@forEach
        }

        if (currentMapper == null) {
            val mapperInfoMatcher = Regex("(.+)-to-(.+) map:").matchEntire(line)
                ?: throw IllegalStateException("Could not find mapper source/destination from line: $line")

            val sourceName = mapperInfoMatcher.groupValues[1]
            val destinationName = mapperInfoMatcher.groupValues[2]
            currentMapper = Mapper(sourceName, destinationName, mutableListOf())
            return@forEach
        }

        val numbers = line.split(" ").map { it.toLong() }
        val destinationRangeStart = numbers[0]
        val sourceRangeStart = numbers[1]
        val rangeSize = numbers[2]
        currentMapperRanges.add(
            MapperRange(
                sourceRange = sourceRangeStart until sourceRangeStart + rangeSize,
                destinationRange = destinationRangeStart until destinationRangeStart + rangeSize,
                sourceToDestinationOffset = destinationRangeStart.toLong() - sourceRangeStart.toLong(),
            )
        )
    }
    currentMapper?.let {
        mappers.add(it.copy(ranges = currentMapperRanges))
    }

    return Input(seeds, mappers)
}

data class Input(
    val seeds: List<Long>,
    val mappers: List<Mapper>,
)

data class Mapper(
    val sourceName: String,
    val destinationName: String,
    val ranges: List<MapperRange>
) {
    fun map(input: Long): Long =
        ranges.find { it.sourceRange.contains(input) }?.let { (input + it.sourceToDestinationOffset) }
            ?: input

    fun mapBack(input: Long): Long =
        ranges.find { it.destinationRange.contains(input) }?.let { (input - it.sourceToDestinationOffset) }
            ?: input

    fun mapRange(range: LongRange): List<LongRange> {
        val applyingRanges = ranges
            .sortedBy { it.sourceRange.first }
            .filter { it.sourceRange.contains(range.first) || it.sourceRange.contains(range.last) || (it.sourceRange.first >= range.first && it.sourceRange.last <= range.last) }

        if (applyingRanges.isEmpty()) {
            return listOf(range)
        }

        val resultRanges = mutableListOf<LongRange>()
        fun addRangeWithOffset(range: LongRange, offset: Long) {
            resultRanges.add((range.first + offset)..(range.last + offset))
        }

        // before
        if (applyingRanges.first().sourceRange.first > range.first) {
            resultRanges.add(range.first until applyingRanges.first().sourceRange.first)
        }

        // ranges
        applyingRanges.forEach { applyingRange ->
            addRangeWithOffset(
                applyingRange.sourceRange.first.coerceAtLeast(range.first)..
                        applyingRange.sourceRange.last.coerceAtMost(range.last),
                applyingRange.sourceToDestinationOffset
            )
        }

        // after
        if (applyingRanges.last().sourceRange.last < range.last) {
            resultRanges.add(applyingRanges.last().sourceRange.last + 1L..range.last)
        }

        if (resultRanges.any { it.isEmpty() }) throw IllegalStateException("is empty")

        return resultRanges
    }
}

data class MapperRange(
    val sourceRange: LongRange,
    val destinationRange: LongRange,
    val sourceToDestinationOffset: Long
)
