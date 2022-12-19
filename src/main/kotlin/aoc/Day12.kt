package aoc.d12

import aoc.DaySolution

object Day12 : DaySolution {
    override fun partOne(input: String) = with(input.parseMap()) { shortestPath(end, start::equals) }
    override fun partTwo(input: String) = with(input.parseMap()) { shortestPath(end) { heightAt(it) == 'a' } }
}

fun String.parseMap() = with(split('\n')) {
    fun locate(c: Char) = indexOfFirst { it.contains(c) }.let { Pos(this[it].indexOf(c), it) }
    Map(map { it.replace('S', 'a').replace('E', 'z') }, locate('S'), locate('E'))
}

fun Map.shortestPath(start: Pos, endPredicate: (Pos) -> Boolean) =
    generateSequence(searchFrom(start), ::augment)
        .indexOfFirst { it.edge.any(endPredicate) }

fun Map.augment(search: Search) =
    search.edge.flatMap(::stepsFrom)
        .filterNot(search.visited::contains)
        .distinct()
        .let(search::augment)

fun Search.augment(positions: Collection<Pos>) = Search(visited + positions, positions)
fun searchFrom(start: Pos) = Search(setOf(start))
data class Search(val visited: Set<Pos>, val edge: Collection<Pos> = visited)

data class Map(val heights: List<String>, val start: Pos, val end: Pos)
fun Map.inBounds(pos: Pos) = pos.y in heights.indices && pos.x in heights.first().indices
fun Map.heightAt(pos: Pos) = heights[pos.y][pos.x]
fun Map.isAllowedMove(from: Pos, to: Pos) = heightAt(from) + 1 >= heightAt(to)
fun Map.stepsFrom(pos: Pos) = Direction.values().map(pos::step).filter(::inBounds).filter { isAllowedMove(it, pos) }

fun Pos.step(dir: Direction) = with(dir) { Pos(x + dx, y + dy) }
data class Pos(val x: Int, val y: Int)

enum class Direction(val dx: Int = 0, val dy: Int = 0) {
    NORTH(dy = -1), EAST(dx = 1), SOUTH(dy = 1), WEST(dx = -1);
}