package aoc.d23

import aoc.DaySolution
import aoc.Pos
import aoc.d23.Dir.*
import aoc.mapPositionedNotNull

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

fun String.toElves() = mapPositionedNotNull { pos, c -> pos.takeIf { c == '#' } }.toSet().let(::Grove)

fun <T> List<T>.rotate() = List(size) { get((it + 1) % size) }
fun List<Int>.range() = min()..max()

data class Grove(val elves: Set<Pos>, val propositions: List<Dir> = preferredDirections)
val preferredDirections = listOf(N, S, W, E)

infix operator fun Pos.plus(dir: Dir) = this + dir.delta
val Pos.adjacents: List<Pos> get() = values().map(this::plus)

fun Dir.test(grove: Grove, elf: Pos) = listOf(elf + this, elf + this.left, elf + this.right).none(grove.elves::contains)
fun Dir.propose(elf: Pos) = elf + this

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