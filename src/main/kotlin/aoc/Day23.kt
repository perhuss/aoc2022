package aoc.d23

import aoc.DaySolution
import aoc.d23.Dir.*

object Day23 : DaySolution {
    override fun partOne(input: String) =
        generateSequence(input.toElves(), Grove::execute).drop(10).first().countEmptyGround()
    override fun partTwo(input: String) =
        generateSequence(input.toElves(), Grove::execute)
            .zipWithNext()
            .indexOfFirst { it.first.elves == it.second.elves } + 1
}

fun Grove.execute(): Grove {
    val proposedMoves = elves.associateWith(::proposeMove).filterValues { it != null } as Map<Pos, Pos>
    val targetCounts = proposedMoves.values.groupingBy { it }.eachCount()
    val actualMoves = proposedMoves.filterValues { targetCounts[it] == 1 }
    return Grove(elves.map { actualMoves.getOrDefault(it, it) }.toSet(), propositions.rotate())
}

fun Grove.countEmptyGround() = smallestBoxArea() - elves.size
fun Grove.smallestBoxArea() = elves.map(Pos::row).range().count() * elves.map(Pos::col).range().count()

fun Grove.proposeMove(elf: Pos): Pos? =
    if(elf.adjacents.none(elves::contains)) null
    else propositions.firstOrNull { it.test(this, elf) }?.propose(elf)

fun String.toElves() =
    lines()
        .flatMapIndexed { row, line -> line.mapIndexedNotNull { col, char -> Pos(row, col).takeIf { char == '#' } } }
        .toSet()
        .let(::Grove)

fun <T> List<T>.rotate() = List(size) { get((it + 1) % size) }
fun List<Int>.range() = min()..max()

data class Grove(val elves: Set<Pos>, val propositions: List<Proposition> = Proposition.values().toList())

data class Pos(val row: Int, val col: Int) {
    infix operator fun plus(dir: Dir) = this + dir.delta
    infix operator fun plus(o: Pos) = Pos(row + o.row, col + o.col)
    val adjacents: List<Pos> get() = Dir.values().map(this::plus)
}

enum class Proposition(val dir: Dir) {
    NORTH(N), SOUTH(S), WEST(W), EAST(E);
    fun test(grove: Grove, elf: Pos) = listOf(elf + dir, elf + dir.left, elf + dir.right).none(grove.elves::contains)
    fun propose(elf: Pos) = elf + dir
}

enum class Dir(val delta: Pos) {
    N(Pos(-1, 0)),
    NE(Pos(-1, 1)),
    E(Pos(0, 1)),
    SE(Pos(1, 1)),
    S(Pos(1, 0)),
    SW(Pos(1, -1)),
    W(Pos(0, -1)),
    NW(Pos(-1, -1));
    val right: Dir get() = values()[(ordinal + 1) % 8]
    val left: Dir get() = values()[(ordinal + 7) % 8]
}