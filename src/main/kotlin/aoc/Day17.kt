package aoc.d17

import aoc.DaySolution
import aoc.d17.Push.LEFT
import aoc.d17.Push.RIGHT

object Day17 : DaySolution {
    override fun partOne(input: String) = input.toCave().heightAfter(2022)
    override fun partTwo(input: String) = input.toCave().heightAfter(1_000_000_000_000)
}

fun Cave.heightAfter(numberOfRocksToDrop: Long): Long {
    data class Key(val rockIndex: Int, val pushIndex: Int, val topRows: List<Int>)
    fun Cave.toKey() = Key(rockIndex, pushIndex, tower.topRows)
    val cache = mutableMapOf<Key, Cave>()
    val (cachedCave, cave) =
        generateSequence(this, Cave::dropRock)
            .map { cache.put(it.toKey(), it) to it }
            .first { it.first != null }
    val cycleSize = cave.rocksDropped - cachedCave!!.rocksDropped
    val cycleHeight = cave.tower.height - cachedCave.tower.height
    val remainingNumberOfRocksToDrop = numberOfRocksToDrop - cave.rocksDropped
    val numberOfCyclesSkipped = remainingNumberOfRocksToDrop / cycleSize
    val higherTower = cave.tower.copy(height = cave.tower.height + cycleHeight * numberOfCyclesSkipped)
    return generateSequence(cave.copy(tower = higherTower), Cave::dropRock)
        .drop(remainingNumberOfRocksToDrop.mod(cycleSize).toInt())
        .first().tower.height
}

fun Cave.dropRock(): Cave {
    var nextPushIndex = pushIndex
    var pos = tower.height + 3
    var rock = Rock.values()[rockIndex].shape
    fun nextPush() = jetPattern[nextPushIndex].also { nextPushIndex = (nextPushIndex + 1) % jetPattern.size }
    fun pushLeft() { if (rock.all { it and 64 == 0 }) rock.map { row -> row shl 1 }.let { if (testRock(it, pos)) rock = it } }
    fun pushRight() { if (rock.all { it and 1 == 0 }) rock.map { it shr 1 }.let { if (testRock(it, pos)) rock = it } }
    fun canFall() = pos > 0 && testRock(rock, pos - 1)

    while(true) {
        if (nextPush() == LEFT) pushLeft() else pushRight()
        if (canFall()) pos -= 1 else break
    }
    return placeRock(rock, pos, (rockIndex + 1) % 5, nextPushIndex)
}

fun Tower.add(rock: List<Int>, pos: Long): Tower {
    return Tower(
        height = (pos + rock.size).coerceAtLeast(height),
        topRows = topRows.toMutableList().apply {
            rock.forEachIndexed { i, rockRow ->
                if (this@add.indexOf(pos + i) in indices)
                    this[this@add.indexOf(pos + i)] = this[this@add.indexOf(pos + i)] or rockRow
                else add(rockRow)
            }
        }.takeLast(100)
    )
}

private fun String.toCave() = Cave(jetPattern = map { if (it == '<') LEFT else RIGHT })

data class Cave(
    val jetPattern: List<Push>,
    val rocksDropped: Long = 0,
    val tower: Tower = Tower(),
    val rockIndex: Int = 0,
    val pushIndex: Int = 0
) {
    fun testRock(rock: List<Int>, pos: Long) = rock.indices.all { tower[pos + it] and rock[it] == 0 }
    fun placeRock(rock: List<Int>, pos: Long, rockIndex: Int, pushIndex: Int) =
        copy(rocksDropped = rocksDropped + 1, tower = tower.add(rock, pos), rockIndex = rockIndex, pushIndex = pushIndex)
}

data class Tower(val height: Long = 0, val topRows: List<Int> = emptyList()) {
    operator fun get(pos: Long) = topRows.getOrElse(indexOf(pos)) { 0 }
    fun indexOf(pos: Long) = (pos - height + topRows.size).toInt()
}

enum class Push { LEFT, RIGHT }

enum class Rock(val shape: List<Int>) {
    MINUS(listOf(0b0011110)),
    PLUS(listOf(0b0001000, 0b0011100, 0b0001000)),
    ANGLE(listOf(0b0011100, 0b0000100, 0b0000100)),
    PIPE(listOf(0b0010000, 0b0010000, 0b0010000, 0b0010000)),
    DOT(listOf(0b0011000, 0b0011000))
}