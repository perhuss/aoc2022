package aoc.d09

import aoc.DaySolution
import kotlin.math.abs

object Day09 : DaySolution {
    override fun partOne(input: String) = solve(input.lineSequence(), 2)
    override fun partTwo(input: String) = solve(input.lineSequence(), 10)
}

fun solve(lines: Sequence<String>, ropeLength: Int = 2) =
    lines.map(String::parseInstruction)
        .flatMap(Instruction::asSequence)
        .runningFold(ropeOfLength(ropeLength), Rope::step)
        .map(Rope::tail)
        .distinct()
        .count()

fun String.parseInstruction() = split(' ').let { (d, s) -> Instruction(Dir.valueOf(d), s.toInt()) }
data class Instruction(val dir: Dir, val steps: Int)
fun Instruction.asSequence() = generateSequence { dir }.take(steps)

data class Rope(val knots: List<Pos>)
fun ropeOfLength(ropeLength: Int) = Rope(List(ropeLength) { Pos(0, 0) })
val Rope.head: Pos get() = knots.first()
val Rope.tail: Pos get() = knots.last()
fun Rope.step(dir: Dir) = Rope(knots.drop(1).runningFold(head.step(dir), Pos::chase))

data class Pos(val x: Int, val y: Int) {
    fun step(dir: Dir) = Pos(x + dir.dx, y + dir.dy)
    fun chase(from: Pos) = when {
        x dist from.x > 1 && y dist from.y > 1 -> Pos(x avg from.x, y avg from.y)
        x dist from.x > 1 -> Pos(x avg from.x, y)
        y dist from.y > 1 -> Pos(x, y avg from.y)
        else -> from
    }
}

enum class Dir(val dx: Int = 0, val dy: Int = 0) {
    U(dy = 1), D(dy = -1), L(dx = -1), R(dx = 1)
}

infix fun Int.dist(o: Int) = abs(this - o)
infix fun Int.avg(o: Int) = (this + o) / 2