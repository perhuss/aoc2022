package aoc.d05

import aoc.DaySolution

object Day05 : DaySolution {
    override fun partOne(input: String) = day5part1(input.lines())
    override fun partTwo(input: String) = day5part2(input.lines())
}

fun day5part1(lines: List<String>): String {
    val stacks = parseStacks(lines)
    return lines.asSequence()
        .dropWhile { !it.startsWith("move") }
        .map { it.removePrefix("move ").split(" from ", " to ").map(String::toInt).let { (a, b, c) -> Instruction(a, b - 1, c - 1) } }
        .fold(stacks) { stacks, instr ->
            stacks.mapIndexed { index, stack ->
                when(index) {
                    instr.from -> stack.dropLast(instr.quantity)
                    instr.to -> stack + stacks[instr.from].takeLast(instr.quantity).reversed()
                    else -> stack
                }
            }
        }
        .map(List<String>::last)
        .joinToString("")
}

fun day5part2(lines: List<String>): String {
    val stacks = parseStacks(lines)
    return lines.asSequence()
        .dropWhile { !it.startsWith("move") }
        .map { it.removePrefix("move ").split(" from ", " to ").map(String::toInt).let { (a, b, c) -> Instruction(a, b - 1, c - 1) } }
        .fold(stacks) { stacks, instr ->
            stacks.mapIndexed { index, stack ->
                when(index) {
                    instr.from -> stack.dropLast(instr.quantity)
                    instr.to -> stack + stacks[instr.from].takeLast(instr.quantity)
                    else -> stack
                }
            }
        }
        .map(List<String>::last)
        .joinToString("")
}

private fun parseStacks(lines: List<String>) = lines.asSequence()
    .takeWhile(String::isNotEmpty)
    .filter { !it.startsWith(" 1") }
    .fold(mutableListOf<List<String>>()) { stacks, line ->
        line.chunked(4)
            .map { it.substring(1, 2) }
            .forEachIndexed { index, crate ->
                run {
                    if (index >= stacks.size) stacks.add(emptyList())
                    if (crate != " ") stacks[index] = listOf(crate) + stacks[index]
                }
            }
        stacks
    }
    .toList()

private data class Instruction(val quantity: Int, val from: Int, val to: Int)