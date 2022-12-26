package aoc.d22

import aoc.DaySolution
import aoc.d22.Direction.*
import aoc.d22.Instruction.*
import aoc.d22.Tile.*
import aoc.d22.Wrapping.CUBIC
import aoc.d22.Wrapping.FLAT
import java.util.*
import java.util.Collections.nCopies

object Day22 : DaySolution {
    override fun partOne(input: String) = input.toNotes(FLAT).findPassword()
    override fun partTwo(input: String) = input.toNotes(CUBIC).findPassword()
}

fun Notes.findPassword() = path.fold(start(), this::execute).toPassword()
fun Walker.toPassword() = 1000 * (pos.row + 1) + 4 * (pos.col + 1) + facing.ordinal
fun Notes.start() = Walker(map.keys.filter { it.row == 0 }.minBy(Pos::col), RIGHT)

fun Notes.execute(walker: Walker, instruction: Instruction) =
    with(walker) {
        fun next() = pos.step(facing).let { if (it in map) copy(pos = it) else wrap(this) }
        when (instruction) {
            TURN_RIGHT -> copy(facing = facing.right())
            TURN_LEFT -> copy(facing = facing.left())
            WALK -> next().takeIf { map[it.pos] == OPEN }?: this
        }
    }

fun Notes.wrap(walker: Walker) = when(wrapping) { FLAT -> wrapFlat(walker); CUBIC -> wrapCubic(walker) }

fun Notes.wrapFlat(walker: Walker) =
    walker.copy(
        pos = when(walker.facing) {
            RIGHT -> map.keys.filter(walker.pos::sameRow).minBy(Pos::col)
            DOWN -> map.keys.filter(walker.pos::sameCol).minBy(Pos::row)
            LEFT -> map.keys.filter(walker.pos::sameRow).maxBy(Pos::col)
            UP -> map.keys.filter(walker.pos::sameCol).maxBy(Pos::row)
        }
    )

fun wrapCubic(walker: Walker): Walker {
    val rowSection = walker.pos.row / 50
    val colSection = walker.pos.col / 50
    val rowRelative = walker.pos.row % 50
    val colRelative = walker.pos.col % 50
    return when(walker.facing) {
        RIGHT -> when(rowSection) {
            0 -> Walker(Pos(149 - rowRelative, 99), LEFT)
            1 -> Walker(Pos(49, 100 + rowRelative), UP)
            2 -> Walker(Pos(49 - rowRelative, 149), LEFT)
            else -> Walker(Pos(149, 50 + rowRelative), UP)
        }
        DOWN -> when(colSection) {
            0 -> Walker(Pos(0, 100 + colRelative), DOWN)
            1 -> Walker(Pos(150 + colRelative, 49), LEFT)
            else -> Walker(Pos(50 + colRelative, 99), LEFT)
        }
        LEFT -> when(rowSection) {
            0 -> Walker(Pos(149 - rowRelative, 0), RIGHT)
            1 -> Walker(Pos(100, rowRelative), DOWN)
            2 -> Walker(Pos(49 - rowRelative, 50), RIGHT)
            else -> Walker(Pos(0, 50 + rowRelative), DOWN)
        }
        UP -> when(colSection) {
            0 -> Walker(Pos(50 + colRelative, 50), RIGHT)
            1 -> Walker(Pos(150 + colRelative, 0), RIGHT)
            else -> Walker(Pos(199, colRelative), UP)
        }
    }
}

fun String.toNotes(wrapping: Wrapping) = split("\n\n").let { (map, path) -> Notes(map.toMap(), path.toPath(), wrapping) }
fun String.toMap() = lines().flatMapIndexed { row, line -> line.toLine(row) }.toMap()
fun String.toLine(row: Int) =
    mapIndexedNotNull { col, char -> if(char != ' ') Pos(row, col) to if(char == '#') WALL else OPEN else null }
fun String.toPath() =
    splitToSequence("""(?=[LR])(?<=\d)|(?=\d)(?<=[LR])""".toRegex())
        .map { when(it) { "L" -> listOf(TURN_LEFT); "R" -> listOf(TURN_RIGHT); else -> nCopies(it.toInt(), WALK) } }
        .flatMap(List<Instruction>::asSequence)
        .asIterable()

class Notes(val map: Map<Pos, Tile>, val path: Iterable<Instruction>, val wrapping: Wrapping)

data class Walker(val pos: Pos, val facing: Direction)
enum class Instruction { TURN_LEFT, TURN_RIGHT, WALK }

data class Pos(val row: Int, val col: Int) {
    fun sameCol(o: Pos) = col == o.col
    fun sameRow(o: Pos) = row == o.row
    fun step(direction: Direction) = this + direction.delta
    infix operator fun plus(o: Pos) = Pos(row + o.row, col + o.col)
}

enum class Direction(val delta: Pos) {
    RIGHT(Pos(0, 1)),
    DOWN(Pos(1, 0)),
    LEFT(Pos(0, -1)),
    UP(Pos(-1, 0));
    fun right() = Direction.values()[(ordinal + 1) % 4]
    fun left() = Direction.values()[(ordinal + 3) % 4]
}
enum class Tile { OPEN, WALL }
enum class Wrapping { FLAT, CUBIC }