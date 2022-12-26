package aoc.d21

import aoc.DaySolution

object Day21 : DaySolution {
    override fun partOne(input: String) = with(input.toContext()) { root.value(this) }
    override fun partTwo(input: String) = input.toContext().fix().findHumanValue()
}

fun Context.fix() =
    Context(
        monkeys = monkeys
                + ("root" to root.copy(job = (root.job as MathJob).copy(operation = '=')))
                + ("humn" to Monkey("humn", NullJob))
    )

fun String.toContext() = Context(lines().map(String::toMonkey).associateBy(Monkey::name))
fun String.toMonkey() = split(": ").let { (name, job) -> Monkey(name, job.toJob()) }
fun String.toJob() = if(any(Char::isDigit)) NumberJob(toLong()) else split(' ').let { (l, o, r) -> MathJob(o.first(), l, r) }

sealed interface Job {
    fun value(context: Context): Long?
    fun findUnknown(context: Context, expectation: Long): Long?
}

data class NumberJob(val number: Long) : Job {
    override fun value(context: Context) = number
    override fun findUnknown(context: Context, expectation: Long) = throw NotImplementedError()
}

data class MathJob(val operation: Char, val left: String, val right: String) : Job {
    override fun value(context: Context): Long? {
        val leftValue = context.getValue(left)
        val rightValue = context.getValue(right)
        return when {
            operation == '=' -> leftValue?: rightValue!!
            rightValue == null || leftValue == null -> null
            operation == '+' -> leftValue + rightValue
            operation == '-' -> leftValue - rightValue
            operation == '*' -> leftValue * rightValue
            else -> leftValue / rightValue
        }
    }

    override fun findUnknown(context: Context, expectation: Long): Long? {
        fun delegate(name: String, expectation: Long) = context.getMonkey(name).job.findUnknown(context, expectation)
        val leftValue = context.getValue(left)
        val rightValue = context.getValue(right)
        val name = if(leftValue == null) left else right
        return when {
            operation == '=' -> delegate(name, expectation)
            operation == '+' -> delegate(name, expectation - (leftValue?: rightValue!!))
            operation == '*' -> delegate(name, expectation / (leftValue?: rightValue!!))
            operation == '-' -> delegate(name, if(leftValue == null) expectation + rightValue!! else leftValue - expectation)
            else -> delegate(name, if(leftValue == null) expectation * rightValue!! else leftValue / expectation)
        }
    }
}

object NullJob: Job {
    override fun value(context: Context) = null
    override fun findUnknown(context: Context, expectation: Long) = expectation
}

data class Monkey(val name: String, val job: Job) {
    fun value(context: Context) = job.value(context)
}
data class Context(val monkeys: Map<String, Monkey>, val mem: MutableMap<String, Long?> = mutableMapOf()) {
    val root: Monkey = getMonkey("root")
    fun findHumanValue() = root.job.findUnknown(this, root.value(this)!!)
    fun getMonkey(name: String) = monkeys[name]!!
    fun getValue(name: String) = mem.getOrPut(name) { monkeys[name]!!.value(this) }
}