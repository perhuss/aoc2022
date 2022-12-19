package aoc.d04

import aoc.DaySolution

object Day04 : DaySolution {
    override fun partOne(input: String) =
        input.lines().map(String::toRanges).count { (a, b) -> a.contains(b) || b.contains(a) }

    override fun partTwo(input: String) =
        input.lines().map(String::toRanges).count { (a, b) -> a.containsAny(b) || b.containsAny(a) }
}

fun String.toRanges() = split(',').map { it.split('-').map(String::toInt) }.map { (a, b) -> a..b }

fun <T: Comparable<T>> ClosedRange<T>.contains(o: ClosedRange<T>) = start <= o.start && o.endInclusive <= endInclusive
fun <T: Comparable<T>> ClosedRange<T>.containsAny(o: ClosedRange<T>) = contains(o.start) || contains(o.endInclusive)