package aoc.d25

import aoc.DaySolution

object Day25 : DaySolution {
    override fun partOne(input: String) = input.lines().map(::desnafufy).sum().snafufy()
}

fun desnafufy(str: String) = str.map { "=-012".indexOf(it) - 2L }.reduce { acc, v -> 5 * acc + v }

fun Long.snafufy(): String =
    if(this == 0L) "" else (this % 5).let { (this / 5 + if (it > 2) 1 else 0).snafufy() + "012=-"[it.toInt()] }
