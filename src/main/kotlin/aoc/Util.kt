package aoc

fun <T: Comparable<T>> Sequence<T>.greatest(n: Int) = sortedDescending().take(n)

val <T> ((T) -> Boolean).negated : (T) -> Boolean get() = { !invoke(it) }

fun <T: Any> Sequence<T>.splitBy(predicate: (T) -> Boolean): Sequence<List<T>> =
    sequence {
        with(iterator()) {
            while(hasNext()) {
                yield(asSequence().takeWhile(predicate.negated).toList())
            }
        }
    }

interface DaySolution {
    fun partOne(input: String): Any = throw NotImplementedError()
    fun partTwo(input: String): Any = throw NotImplementedError()
}