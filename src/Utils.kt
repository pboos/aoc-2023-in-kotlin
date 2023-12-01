import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readLines

/**
 * Reads lines from the given input txt file.
 */
fun readInput(day: Int, isTest: Boolean = false): List<String> {
    val dayString = day.toString().padStart(2, '0')
    val testAppendix = if (isTest) "_test" else ""
    return Path("src/day$dayString/Day$dayString$testAppendix.txt").readLines()
}

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
