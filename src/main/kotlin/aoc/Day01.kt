package aoc.d01

import aoc.DaySolution
import aoc.greatest
import aoc.splitBy

object Day01: DaySolution {
    override fun partOne(input: String) = parseElvenCalories(input).max()
    override fun partTwo(input: String) = parseElvenCalories(input).greatest(3).sum()
}

fun parseElvenCalories(input: String) =
    input.lineSequence().splitBy(String::isEmpty).map { it.sumOf(String::toInt) }
