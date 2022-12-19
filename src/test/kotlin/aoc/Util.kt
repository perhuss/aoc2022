package aoc

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assumptions.assumeThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DayTest(
    val day: DaySolution,
    val partOneExample: Any? = null,
    val partOne: Any? = null,
    val partTwoExample: Any? = null,
    val partTwo: Any? = null
) {
    val example: String
    val input: String
    init {
        fun fetchInput(suffix: String) =
            this::class.java.getResourceAsStream("${day::class.simpleName}-$suffix.txt").reader().readText()
        example = fetchInput("example")
        input = fetchInput("input")
    }

    @ParameterizedTest(name = "{0} expecting {3}")
    @MethodSource("testParams")
    fun test(name: String, method: (String) -> Any, input: String, expectation: Any?) {
        assumeThat(expectation).isNotNull()
        assertThat(method(input)).isEqualTo(expectation)
    }

    fun testParams() =
        listOf(
            arguments("partOneExample", day::partOne, example, partOneExample),
            arguments("partOne", day::partOne, input, partOne),
            arguments("partTwoExample", day::partTwo, example, partTwoExample),
            arguments("partTwo", day::partTwo, input, partTwo)
        )
}