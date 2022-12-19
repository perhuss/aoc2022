package aoc.d13

import aoc.DaySolution
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

object Day13 : DaySolution {
    override fun partOne(input: String) =
        input.splitToSequence("\n\n")
            .map { it.split('\n').map(PacketGrammar::parseToEnd) }
            .indicesOf { (a, b) -> test(a, b) }
            .sum()

    override fun partTwo(input: String) =
        input.splitToSequence("\n")
            .filterNot(String::isEmpty)
            .map(PacketGrammar::parseToEnd)
            .plus(dividers)
            .sortedWith(packetOrder)
            .indicesOf(dividers::contains)
            .reduce(Int::times)
}

fun <T> Sequence<T>.indicesOf(p: (T) -> Boolean) = mapIndexedNotNull { i, v -> (i + 1).takeIf { p(v) }}
val dividers = listOf("[[2]]", "[[6]]").map(PacketGrammar::parseToEnd)
val packetOrder = Comparator<Packet> { a, b -> if(test(a, b)) -1 else 1 }

object PacketGrammar : Grammar<Packet>() {
    private val lbr by literalToken("[")
    private val rbr by literalToken("]")
    private val comma by literalToken(",")
    private val num by regexToken("[0-9]+")

    private val lst by separatedTerms(parser(::packet), comma, acceptZero = true)
    private val packet: Parser<Packet> by num use { IntPacket(text.toInt()) } or
            ((-lbr * lst * -rbr) use { ListPacket(this) } )

    override val rootParser by packet
}

fun test(a: Packet, b: Packet, tiebreaker: () -> Boolean = { false }) =
    when {
        a is IntPacket && b is IntPacket -> if (a == b) tiebreaker() else a.value < b.value
        else -> testList(a.toList(), b.toList(), tiebreaker)
    }

fun testList(a: List<Packet>, b: List<Packet>, tiebreaker: () -> Boolean): Boolean =
    when {
        a.isEmpty() && b.isEmpty() -> tiebreaker()
        a.isEmpty() -> true
        b.isEmpty() -> false
        else -> test(a.first(), b.first()) { testList(a.drop(1), b.drop(1), tiebreaker) }
    }

fun Packet.toList() = when(this) { is ListPacket -> packets; is IntPacket -> listOf(this) }
sealed interface Packet
data class ListPacket(val packets: List<Packet>) : Packet
data class IntPacket(val value: Int) : Packet