package aoc.d06

import aoc.DaySolution

object Day06 : DaySolution {
    override fun partOne(input: String) = input.indexOfMarker(4)
    override fun partTwo(input: String) = input.indexOfMarker(14)
}

fun String.indexOfMarker(size: Int) = windowedSequence(size).indexOfFirst { it.toSet().size == size } + size
