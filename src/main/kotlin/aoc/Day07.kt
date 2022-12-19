package aoc.d07

import aoc.DaySolution

object Day07 : DaySolution {
    override fun partOne(input: String) = input.lineSequence().parseTree().sizes().filter { it <= 100_000 }.sum()
    override fun partTwo(input: String) = input.lineSequence().parseTree().sizes().run {
        val diskToFree = last() - 40_000_000
        sorted().first { it >= diskToFree }
    }
}

fun Sequence<String>.parseTree(): Dir =
    fold(listOf(Dir("/"))) { path, line ->
        when {
            line == "$ ls" -> path
            line == "$ cd /" -> path.subList(0, 1)
            line == "$ cd .." -> path.dropLast(1)
            line.startsWith("$ cd") ->
                path + path.last().nodes.first {
                    it is Dir && it.name == line.removePrefix("$ cd ")
                } as Dir
            else -> path.apply { last().nodes.add(line.parseNode()) }
        }
    }
        .first()

fun String.parseNode() =
    if (startsWith("dir")) Dir(removePrefix("dir "))
    else File(takeWhile(Char::isDigit).toLong())

fun Dir.sizes(): List<Long> = nodes.filterIsInstance<Dir>().flatMap(Dir::sizes) + size

sealed interface Node { val size: Long }
class File(override val size: Long) : Node
class Dir(val name: String, var nodes: MutableList<Node> = mutableListOf()) : Node {
    override val size: Long get() = nodes.sumOf(Node::size)
}