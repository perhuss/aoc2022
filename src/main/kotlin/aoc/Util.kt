package aoc

interface DaySolution {
    fun partOne(input: String): Any? = throw NotImplementedError()
    fun partTwo(input: String): Any? = throw NotImplementedError()
}

fun <T> String.mapPositionedNotNull(transform: (Pos, Char) -> T?): List<T> =
    lines().flatMapIndexed { row, line -> line.mapIndexedNotNull { col, char -> transform(Pos(row, col), char) } }

fun String.withPosition(): List<PositionedValue<Char>> =
    lines().flatMapIndexed { row, line -> line.mapIndexed { col, char -> PositionedValue(Pos(row, col), char) } }

data class PositionedValue<T>(val pos: Pos, val value: T)

data class Pos(val row: Int, val col: Int) {
    infix operator fun plus(o: Pos) = Pos(row + o.row, col + o.col)
}
