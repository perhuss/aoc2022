package aoc.d11

import aoc.DaySolution
import kotlin.text.RegexOption.MULTILINE

object Day11 : DaySolution {
    override fun partOne(input: String) =
        Round(input.parseMonkeys(), relax = { it / 3 }).computeMonkeyBusiness(20)

    override fun partTwo(input: String) =
        input.parseMonkeys().let { monkeys ->
            val modulo = monkeys.map(Monkey::test).map(Test::modulo).reduce(Long::times)
            Round(monkeys, relax = { it % modulo }).computeMonkeyBusiness(10000)
        }
}

fun Round.computeMonkeyBusiness(rounds: Int) =
    generateSequence(this, Round::play)
        .drop(1)
        .take(rounds)
        .flatMap(Round::handovers)
        .groupingBy(Handover::from)
        .eachCount()
        .values
        .sortedDescending()
        .take(2)
        .map(Int::toLong)
        .reduce(Long::times)

fun Round.play(): Round {
    fun Monkey.inspect(worry: Worry) = handover(relax(op(worry)))
    val items = monkeys.map(Monkey::items).map(List<Worry>::toMutableList)
    val handovers = mutableListOf<Handover>()
    monkeys.forEachIndexed { index, monkey ->
        items[index].map(monkey::inspect).onEach { (_, to, worry) -> items[to].add(worry) }.also(handovers::addAll)
        items[index].clear()
    }
    return copy(
        monkeys = monkeys.mapIndexed { index, monkey -> monkey.copy(items = items[index].toList()) },
        handovers = handovers
    )
}

fun Monkey.handover(item: Worry) = Handover(number, test.targetFor(item), item)
fun Test.targetFor(worry: Worry) = if(worry isDivisibleBy modulo) targets.first else targets.second
fun String.parseMonkeys() = split("\n\n").map(String::parseMonkey)
fun String.parseMonkey() =
    regex.matchEntire(this)!!.destructured.let { (number, items, operator, operand, test, ifTrue, ifFalse) ->
        Monkey(
            number = number.toInt(),
            items = items.split(", ").map(String::toLong),
            op = {
                when {
                    operator == "*" && operand == "old" -> it * it
                    operator == "*" -> it * operand.toInt()
                    else -> it + operand.toInt()
                }
            },
            test = Test(test.toLong(), ifTrue.toInt() to ifFalse.toInt())
        )
    }
val regex = (".*(.):\n.*: (.+)\n.* = old (.) (.+)\n.* by (.+)\n.*(.)\n.*(.)").toRegex(MULTILINE)
typealias Worry = Long
data class Round(val monkeys: List<Monkey>, val relax: (Worry) -> Worry, val handovers: List<Handover> = emptyList())
data class Monkey(val number: Int, val items: List<Worry>, val op: (Worry) -> Worry, val test: Test)
data class Test(val modulo: Long, val targets: Pair<Int, Int>)
data class Handover(val from: Int, val to: Int, val worry: Worry)
infix fun Long.isDivisibleBy(n: Long) = this % n == 0L