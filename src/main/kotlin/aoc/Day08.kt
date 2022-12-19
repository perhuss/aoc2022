package aoc.d08

import aoc.DaySolution

object Day08 : DaySolution {
    override fun partOne(input: String) =
        with(parseForest(input.lineSequence())) { positions().count(::isTreeVisible) }

    override fun partTwo(input: String) =
        with(parseForest(input.lineSequence())) { positions().maxOf(::scenicScore) }
}

fun parseForest(lines: Sequence<String>) = Forest(lines.toList())

data class Position(val row: Int, val col: Int) {
    fun path(direction: Direction) = generateSequence(this, direction::step).drop(1)
}

enum class Direction(val rowDelta: Int = 0, val colDelta: Int = 0) {
    NORTH(rowDelta = -1), EAST(colDelta = 1), SOUTH(rowDelta = 1), WEST(colDelta = -1);
    fun step(pos: Position) = with(pos) { Position(row + rowDelta, col + colDelta) }
}

class Forest(val trees: List<String>) {
    fun positions() = trees.indices.flatMap { row -> trees[row].indices.map { Position(row, it) } }
    fun treeAt(pos: Position) = trees.getOrNull(pos.row)?.getOrNull(pos.col)
    fun treePath(pos: Position, dir: Direction) = pos.path(dir).map(::treeAt).takeWhile { it != null }.filterNotNull()
    fun isTreeVisible(pos: Position) = with(treeAt(pos)!!) {
        Direction.values().any {
            treePath(pos, it).all { it < this }
        }
    }
    fun scenicScore(pos: Position) = with(treeAt(pos)!!) {
        Direction.values()
            .map {
                val visibleTrees = treePath(pos, it).toList()
                if(visibleTrees.all { it < this }) visibleTrees.size
                else visibleTrees.takeWhile { it < this }.count() + 1
            }
            .fold(1, Int::times)
    }
}