package aoc.d17

import aoc.DaySolution
import aoc.d17.Push.*

object Day17 : DaySolution {
    override fun partOne(input: String) =
        with(Cave(tower = mutableListOf(), jet = input.toJetSequence().iterator())) {
            generateSequence { RockShape.values() }
                .flatMap { it.toList() }
                .take(2022)
//                .onEach { printIt() }
                .forEach(::handleRock)
            tower.size
        }
}

fun Cave.printIt() {
    tower.reversed()
        .map { "%7s".format(it.toString(2).replace('0', '.')).replace(' ', '.') }
        .forEach(::println)
        .also { println() }
}

fun Cave.handleRock(rockShape: RockShape) {
    var pos = tower.size + 3
    var rock = rockShape.shape
    while(true) {
        // push
        val push = jet.next()
        if(push == LEFT) {
            if(rock.all { it and 64 == 0 }) {
                val newRock = rock.map { row -> row shl 1 }
                if (testRock(newRock, pos))
                    rock = newRock
            }
        }
        else if(rock.all { it and 1 == 0 }) {
            val newRock = rock.map { row -> row shr 1 }
            if(testRock(newRock, pos))
                rock = newRock
        }

        // fall
        when {
            pos == 0 -> break
            testRock(rock, pos - 1) -> pos -= 1
            else -> break
        }
    }
    placeRock(rock, pos)
}

fun Cave.testRock(rock: List<Row>, pos: Int) =
    rock.indices.all { (pos + it) !in tower.indices || tower[pos + it] and rock[it] == 0 }

fun Cave.placeRock(rock: List<Row>, pos: Int) =
    rock.indices.forEach { if(pos + it in tower.indices) tower[pos + it] = tower[pos + it] or rock[it] else tower.add(rock[it]) }

fun String.toJetSequence() = generateSequence { map(Char::toPush) }.flatMap { it }

data class Cave(val tower: Tower, val jet: Iterator<Push>)

typealias Row = Int
typealias Tower = MutableList<Row>

fun Char.toPush() = if (this == '<') LEFT else RIGHT
enum class Push {
    LEFT, RIGHT
}

enum class RockShape(val shape: List<Row>) {
    MINUS(listOf(0b0011110)),
    PLUS(listOf(0b0001000, 0b0011100, 0b0001000)),
    ANGLE(listOf(0b0011100, 0b0000100, 0b0000100)),
    PIPE(listOf(0b0010000, 0b0010000, 0b0010000, 0b0010000)),
    DOT(listOf(0b0011000, 0b0011000))
}