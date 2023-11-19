package aoc.d01

import aoc.DaySolution

object Day01: DaySolution {
    override fun partOne(input: String) = parseElvenCalories(input).max()
    override fun partTwo(input: String) = parseElvenCalories(input).sortedDescending().take(3).sum()
}

fun parseElvenCalories(input: String) =
    input.splitToSequence("\n\n").map { it.lines().sumOf(String::toInt) }
