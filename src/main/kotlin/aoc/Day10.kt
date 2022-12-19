package aoc.d10

import aoc.DaySolution

object Day10 : DaySolution {
    override fun partOne(input: String) =
        input.lineSequence()
            .execTrackRegister()
            .mapIndexed(::Pair)
            .filter { (i, _) -> i + 1 in extractionPoints }
            .sumOf { (i, x) -> (i + 1) * x }

    override fun partTwo(input: String) =
        input.lineSequence()
            .execTrackRegister()
            .chunked(40)
            .map { it.mapIndexed { i, x -> if(i in (x-1)..(x+1)) '#' else '.' } }
            .map { it.joinToString("") }
            .take(6)
            .toList()
}

private fun Sequence<String>.execTrackRegister() =
    map(String::toInstruction)
        .flatMap(Instruction::registerMods)
        .runningFold(1, Int::plus)

val extractionPoints = 20..220 step 40
fun String.toInstruction() = if(this == "noop") Noop else Addx(this.removePrefix("addx ").toInt())
fun Instruction.registerMods() = if(this is Addx) listOf(0, v) else listOf(0)

sealed interface Instruction
object Noop: Instruction
data class Addx(val v: Int): Instruction