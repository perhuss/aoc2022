package aoc.d24

import aoc.DaySolution
import aoc.Pos
import aoc.d24.Dir.*
import aoc.mapPositionedNotNull

object Day24 : DaySolution {
    override fun partOne(input: String) = input.toValley().runToExit().time
    override fun partTwo(input: String) = input.toValley().runToExitWithSnacks().time
}

fun String.toValley() = mapPositionedNotNull(::parseBlizzard).let(::Valley)
fun Valley.runToExit() = generateSequence(this, Valley::next).first(Valley::expeditionReachedExit)
fun Valley.runToExitWithSnacks() = generateSequence(this) { it.runToExit().flip() }.elementAt(3)

val Valley.expeditionReachedExit: Boolean get() = exit in possiblePositions
fun Valley.flip() = copy(entrance = exit, exit = entrance, possiblePositions = setOf(exit))

fun parseBlizzard(pos: Pos, char: Char) =
    when(char) {
        '<' -> Blizzard(pos, WEST)
        '>' -> Blizzard(pos, EAST)
        '^' -> Blizzard(pos, NORTH)
        'v' -> Blizzard(pos, SOUTH)
        else -> null
    }

fun Valley.next(): Valley {
    val nextBlizzards = blizzards.map { it.next(limits) }
    val nextPossiblePositions =
        possiblePositions
            .flatMap { values().map(it::plus) + it }
            .filterNot(nextBlizzards.map(Blizzard::pos).toSet()::contains)
            .filter(::isValidPos)
            .toSet()
    return copy(blizzards = nextBlizzards, time = time + 1, possiblePositions = nextPossiblePositions)
}

data class Blizzard(val pos: Pos, val dir: Dir) {
    fun next(limits: Limits) = copy(pos = (pos + dir).takeIf(limits::contains)?: respawnPos(limits))
    fun respawnPos(limits: Limits) = when(dir) {
        NORTH -> pos.copy(row = limits.max.row)
        SOUTH -> pos.copy(row = limits.min.row)
        WEST -> pos.copy(col = limits.max.col)
        EAST -> pos.copy(col = limits.min.col)
    }
}

fun Valley.isValidPos(pos: Pos) = pos in limits || pos == exit || pos == entrance

data class Valley(
    val blizzards: List<Blizzard>,
    val time: Int = 0,
    val limits: Limits = Limits(max = blizzards.map(Blizzard::pos).let { Pos(it.maxOf(Pos::row), it.maxOf(Pos::col)) }),
    val entrance: Pos = Pos(0, 1),
    val exit: Pos = limits.max + SOUTH,
    val possiblePositions: Set<Pos> = setOf(entrance)
)

data class Limits(val min: Pos = Pos(1, 1), val max: Pos) {
    operator fun contains(element: Pos) = element.row in min.row..max.row && element.col in min.col..max.col
}

infix operator fun Pos.plus(dir: Dir) = this + dir.delta

enum class Dir(val delta: Pos) {
    NORTH(Pos(-1, 0)),
    EAST(Pos(0, 1)),
    SOUTH(Pos(1, 0)),
    WEST(Pos(0, -1));
}