package aoc.d02

import aoc.DaySolution
import aoc.d02.Hand.*
import aoc.d02.Outcome.*

object Day02 : DaySolution {
    override fun partOne(input: String) =
        input.lines().map(::parseRound).map(Round::score).sum()
    override fun partTwo(input: String) =
        input.lines().map(::parseStrategy).map(Strategy::toRound).map(Round::score).sum()
}

fun parseRound(str: String) = str.split(' ').map(::parseHand).let { (o, p) -> Round(o, p) }
fun parseStrategy(str: String) = str.split(' ').let { (o, r) -> Strategy(parseHand(o), parseOutcome(r)) }
fun parseHand(str: String) = when(str) {"A", "X" -> ROCK; "B", "Y" -> PAPER; else -> SCISSORS }
fun parseOutcome(str: String) = when(str) { "X" -> LOSE; "Y" -> DRAW; else -> WIN}

fun Round.outcome() = when { your.beats(his) -> WIN; your == his -> DRAW; else -> LOSE }
fun Round.score() = outcome().score + your.score

fun Strategy.toRound() = Round(his, requiredPlay())
fun Strategy.requiredPlay() =
    when(outcome) { WIN -> valueSuchAs { it.beats(his) }; DRAW -> his; LOSE -> valueSuchAs(his::beats) }
fun Hand.beats(his: Hand) = his == when(this) { ROCK -> SCISSORS; SCISSORS -> PAPER; PAPER -> ROCK }

data class Round(val his: Hand, val your: Hand)
data class Strategy(val his: Hand, val outcome: Outcome)
enum class Hand(val score: Int) { ROCK(1), PAPER(2), SCISSORS(3) }
enum class Outcome(val score: Int) { LOSE(0), DRAW(3), WIN(6) }

inline fun <reified E: Enum<E>> valueSuchAs(predicate: (E) -> Boolean): E = enumValues<E>().first(predicate)