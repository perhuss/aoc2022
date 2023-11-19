package aoc.d18

import aoc.DaySolution

object Day18 : DaySolution {
    override fun partOne(input: String) = input.toCubeSet().numberOfVisibleFaces()
    override fun partTwo(input: String) = input.toCubeSet().apply(CubeSet::fillDroplet).numberOfVisibleFaces()
}

typealias CubeSet = MutableSet<Cube>
fun CubeSet.numberOfVisibleFaces() = asSequence().map { it.adjacentCubes().count { it !in this } }.sum()
fun CubeSet.fillDroplet() = toSet().asSequence().flatMap(Cube::adjacentCubes).map(::fillAt).forEach(::addAll)
fun CubeSet.fillAt(start: Cube): Set<Cube> =
    if(start in this) emptySet()
    else generateSequence(BreadthFirst(setOf(start)), ::augment)
        .takeWhile { it.ack.size < 2000 }
        .firstOrNull { it.rim.isEmpty() }
        ?.ack ?: emptySet()
fun CubeSet.augment(search: BreadthFirst) =
    search.rim.flatMap(Cube::adjacentCubes).filterNot { it in this || it in search.ack }.toSet()
        .let { BreadthFirst(it, search.ack + it) }

data class BreadthFirst(val rim: Set<Cube>, val ack: Set<Cube> = rim)

fun String.toCubeSet() = lines().map(String::toCube).toMutableSet()
fun String.toCube() = split(',').map(String::toInt).let { (x, y, z) -> Cube(x, y, z) }

data class Cube(val x: Int, val y: Int, val z: Int) {
    fun adjacentCubes() =
        listOf(copy(x = x - 1), copy(x = x + 1), copy(y = y - 1), copy(y = y + 1), copy(z = z - 1), copy(z = z + 1))
}