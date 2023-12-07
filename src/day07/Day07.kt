package day07

import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        val players = parseInput(input)

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
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput(7, isTest = true)
    check(part1(testInput) == 6440)
//    check(part2(testInput) == 71503)

    val input = readInput(7)
    part1(input).println()
//    part2(input).println()
}

fun parseInput(input: List<String>): List<Player> {
    return input.map { line ->
        val (hand, bid) = line.split(" ")
        Player(hand = hand.toList(), bid = bid.toInt())
    }
}

data class Player(
    val hand: List<Char>,
    val bid: Int,
) {
    companion object {
        private val cards = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
        private val cardStrength = cards.mapIndexed { index, card -> card to (cards.size - index) }.associate { it }
    }

    val handType: Int
        get() {
            val counts = hand
                .fold(mutableMapOf<Char, Int>()) { acc, card ->
                    acc[card] = acc.getOrDefault(card, 0) + 1
                    acc
                }
                .map { it.value }
                .sortedDescending()
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

        for (index in hand.indices) {
            val card = hand[index]
            val otherCard = other.hand[index]
            val strength = cardStrength[card]!!
            val otherStrength = cardStrength[otherCard]!!
            if (strength > otherStrength) return true
            else if (strength < otherStrength) return false
        }

        throw IllegalStateException("both hands the same! $hand === ${other.hand}")
    }
}
