package aoc.d03

import aoc.DaySolution

object Day03 : DaySolution {
    override fun partOne(input: String) =
        input.lines()
            .map { it.chunked(it.length / 2) }
            .map(List<String>::commonChar)
            .map(Char::prio)
            .sum()

    override fun partTwo(input: String) =
        input.lines()
            .chunked(3)
            .map(List<String>::commonChar)
            .map(Char::prio)
            .sum()
}

fun Char.prio() = if (this in 'a'..'z') this - 'a' + 1 else this - 'A' + 27
fun List<String>.commonChar() = map(String::toSet).reduce(Set<Char>::intersect).first()
