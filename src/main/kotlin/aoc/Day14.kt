package aoc.d14

import aoc.DaySolution
import aoc.d14.Direction.*
import kotlin.math.sign

object Day14 : DaySolution {
    override fun partOne(input: String) = with(input.toCave()) { sandAddingSequence().indexOfFirst(::onFloor) }
    override fun partTwo(input: String) = input.toCave().sandAddingSequence().count()
}

data class Cave(val obstacles: MutableSet<Pos>) {
    private val limit: Int = obstacles.maxOf(Pos::y) + 1
    private val sandSource = Pos(500, 0)
    fun onFloor(pos: Pos) = pos.y == limit
    fun sandAddingSequence() = generateSequence { if(sandSource in obstacles) null else addSand() }
    fun addSand() = generateSequence(sandSource, ::dropStep).last().also(obstacles::add)
    fun dropStep(pos: Pos) = if(onFloor(pos)) null else pos.allSteps().firstOrNull { it !in obstacles }
}

data class Pos(val x: Int, val y: Int) {
    fun allSteps() = Direction.values().map(this::step)
    fun step(dir: Direction) = Pos(x + dir.dx, y + dir.dy)
    fun pull(o: Pos) = Pos(x pull o.x, y pull o.y)
    private infix fun Int.pull(o: Int) = o + (this - o).sign
}

enum class Direction(val dx: Int, val dy: Int) {
    DOWN(0, 1), DOWN_LEFT(-1, 1), DOWN_RIGHT(1, 1);
}

fun String.toCave() = Cave(lineSequence().flatMap(String::toPath).toMutableSet())
fun String.toPath() = split(" -> ").map(String::toPos).zipWithNext().flatMap(Pair<Pos, Pos>::line)
fun String.toPos() = split(",").let { (x, y) -> Pos(x.toInt(), y.toInt()) }
fun Pair<Pos, Pos>.line() = generateSequence(first, second::pull).takeWhile { it != second } + second