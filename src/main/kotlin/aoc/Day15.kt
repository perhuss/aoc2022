package aoc.d15

import aoc.DaySolution
import kotlin.math.abs

object Day15 : DaySolution {
    override fun partOne(input: String) =
        with(input.toSpace()) {
            val line = 2000000
            val beaconsInLine = sensors.map(Sensor::beacon).filter { it.y == line }.distinct()
            ranges(line).map(IntRange::count).sum() - beaconsInLine.size
        }

    override fun partTwo(input: String) =
        with(input.toSpace()) {
            val scope = 0..4000000
            scope.reversed().asSequence()
                .map { it to ranges(it).mapNotNull(scope::constrain) }
                .first { it.second.size == 2 }
                .let { (row, ranges) -> row + 4000000L * (ranges.first().last + 1) }
        }
}

fun IntRange.constrain(o: IntRange) = when {
    contains(o) -> o
    !intersects(o) -> null
    else -> first.coerceAtLeast(o.first)..last.coerceAtMost(o.last)
}

class Space(val sensors: List<Sensor>) {
    fun ranges(line: Int) =
        mutableListOf<IntRange>()
            .apply { sensors.mapNotNull { it.rangeOrNull(line) }.sortedBy(IntRange::first).forEach(::addRange) }
}

fun MutableList<IntRange>.addRange(r: IntRange) {
    if (isEmpty()) add(r) else last().tryMerge(r)?.let { this[lastIndex] = it }?: add(r)
}

data class Sensor(val pos: Pos, val beacon: Pos) {
    private val distance = abs(pos.x - beacon.x) + abs(pos.y - beacon.y)
    fun rangeOrNull(row: Int) = reachOn(row).let { if(it < 0) null else (pos.x-it)..(pos.x+it) }
    private fun reachOn(row: Int) = distance - abs(pos.y - row)
}

fun String.toSpace() = Space(lines().map(String::toSensor).sortedBy { it.pos.x })
fun String.toSensor() =
    "(-?\\d+)".toRegex()
        .findAll(this)
        .map(MatchResult::value)
        .map(String::toInt)
        .toList()
        .let { (sx, sy, bx, by) -> Sensor(Pos(sx, sy), Pos(bx, by)) }

data class Pos(val x: Int, val y: Int)

fun IntRange.tryMerge(o: IntRange) =
    if(o.first in this || last + 1 == o.first) first.coerceAtMost(o.first)..last.coerceAtLeast(o.last)
    else null
private fun IntRange.contains(o: IntRange) = first <= o.first && o.last <= last
private fun IntRange.intersects(o: IntRange) = o.first in this || o.last in this || first in o || last in o