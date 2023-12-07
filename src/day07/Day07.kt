package day07

import println
import readInput

fun main() {
    fun part1(input: List<String>, useJokerRule: Boolean = false): Int {
        val players = parseInput(input, useJokerRule)

        val totalPlayers = players.size
        return players
            .sortedWith { player1, player2 ->
                if (player1.isStrongerHandThan(player2)) 1 else -1
            }
            .reversed()
            .mapIndexed { index, player ->
                val rank = totalPlayers - index
                player.bid * rank
            }
            .sum()
    }

    fun part2(input: List<String>): Int {
        return part1(input, useJokerRule = true)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(7, isTest = true)
    check(part1(testInput) == 6440)
    check(part2(testInput) == 5905)

    val input = readInput(7)
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>, useJokerRule: Boolean = false): List<Player> {
    return input.map { line ->
        val (hand, bid) = line.split(" ")
        Player(hand = hand.toList(), bid = bid.toInt(), useJokerRule = useJokerRule)
    }
}

data class Player(
    val hand: List<Char>,
    val bid: Int,
    val useJokerRule: Boolean = false,
) {
    companion object {
        private const val JOKER = 'X'
        private val cards = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2', JOKER /* joker */)
        private val cardStrength = cards.mapIndexed { index, card -> card to (cards.size - index) }.associate { it }
    }

    private val handToUse = if (useJokerRule) hand.map { if (it == 'J') JOKER else it } else hand

    private val handType: Int
        get() {
            val counts = handToUse
                .asSequence()
                .filter { it != JOKER }
                .fold(mutableMapOf<Char, Int>()) { acc, card ->
                    acc[card] = acc.getOrDefault(card, 0) + 1
                    acc
                }
                .map { it.value }
                .sortedDescending()
                .toMutableList()

            if (useJokerRule && handToUse.contains(JOKER)) {
                val jokerCount = handToUse.count { it == JOKER }
                if (counts.isEmpty()) {
                    counts.add(jokerCount)
                } else
                    counts[0] += jokerCount
            }

            return when {
                counts[0] == 5 -> 6
                counts[0] == 4 -> 5
                counts[0] == 3 && counts[1] == 2 -> 4
                counts[0] == 3 && counts[1] == 1 -> 3
                counts[0] == 2 && counts[1] == 2 -> 2
                counts[0] == 2 && counts[1] == 1 -> 1
                else -> 0
            }
        }

    fun isStrongerHandThan(other: Player): Boolean {
        if (handType > other.handType) return true
        else if (handType < other.handType) return false

        for (index in handToUse.indices) {
            val card = handToUse[index]
            val otherCard = other.handToUse[index]
            val strength = cardStrength[card]!!
            val otherStrength = cardStrength[otherCard]!!

            if (strength > otherStrength) return true
            else if (strength < otherStrength) return false
        }

        throw IllegalStateException("both hands the same! $hand === ${other.hand}")
    }
}
