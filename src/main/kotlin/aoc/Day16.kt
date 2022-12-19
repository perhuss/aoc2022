package aoc.d16

import aoc.DaySolution

object Day16 : DaySolution {
    override fun partOne(input: String) =
        input.toSystem().eliminateBrokenValves().complete().run { elephantize(Worker(valves.first()!!, busyFor = 0), null, 30) }

    override fun partTwo(input: String) =
        input.toSystem()
            .eliminateBrokenValves()
            .complete()
            .let(System::elephantizeStart)
}

@JvmRecord
data class Worker(val target: Valve, val busyFor: Int = 0)

fun System.mask() = valves.filterNotNull().drop(1).map { 1L shl it.index }.reduce(Long::or)

fun System.elephantizeStart(): Int {
    val tunnels = valves.first()!!.tunnels.filter { it.to != 0 }.sortedBy(Tunnel::cost)
    val everything = mask()
    return tunnels.dropLast(1)
        .flatMapIndexed { i, t1 -> tunnels.drop(i + 1).map { t2 -> t1 to t2 } }
        .parallelStream()
        .map { (t1, t2) ->
            elephantize(
                Worker(valves[t1.to]!!, busyFor = t1.cost + 1),
                Worker(valves[t2.to]!!, busyFor = t2.cost + 1),
                timeLeft = 26,
                flow = 0,
                targets = everything xor (1L shl t1.to) xor (1L shl t2.to)
            )
        }
        .reduce(Int::coerceAtLeast)
        .get()
}

fun System.elephantize(
    w1: Worker = Worker(valves.first()!!),
    w2: Worker? = Worker(valves.first()!!),
    timeLeft: Int = 26,
    flow: Int = 0,
    targets: Long = mask(),
    ack: Int = 0
): Int {
    val timeToWait = w1.busyFor
    return when {
        timeLeft < 16 && ack < 281 -> 0
        timeToWait >= timeLeft -> ack + timeLeft * flow
        timeToWait == 0 -> {
            val newFlow = flow + w1.target.flow
            if(targets == 0L) {
                if(w2 != null) elephantize(w2, null, timeLeft, newFlow, 0, ack) else ack + timeLeft * newFlow
            } else
                w1.target.tunnels.filter { (1L shl it.to) and targets != 0L }.maxOf { t ->
                    val target = valves[t.to]!!
                    val worker = Worker(target, busyFor = t.cost + 1)
                    val newTargets = targets xor (1L shl target.index)
                    if(w2 == null || t.cost < w2.busyFor)
                        elephantize(worker, w2, timeLeft, newFlow, newTargets, ack)
                    else
                        elephantize(w2, worker, timeLeft, newFlow, newTargets, ack)
                }
        }
        else ->
            elephantize(
                w1 = w1.copy(busyFor = 0),
                w2 = w2?.copy(busyFor = w2.busyFor - timeToWait),
                timeLeft = timeLeft - timeToWait,
                flow = flow,
                targets = targets,
                ack = ack + timeToWait * flow
            )
    }
}

fun String.toSystem() = with(lines().sorted()) {
    val names = map(regex::matchEntire).map { it!!.groups[1]!!.value }.toList()
    val valves = map { it.toValve(names) }.toList()
    System(names, valves)
}
fun String.toValve(names: List<String>) =
    regex.matchEntire(this)!!.destructured.let { (name, flow, tunnels) ->
        Valve(names.indexOf(name), flow.toInt(), tunnels.split(", ").map(names::indexOf).map(::Tunnel))
    }

fun System.complete() = generateSequence(this, System::augment).drop(valves.size).first()
fun System.augment() =
    copy(
        valves = valves.map { v ->
            v?.copy(
                tunnels = v.tunnels + v.tunnels.flatMap { t ->
                    valves[t.to]!!.tunnels
                        .filter { t2 -> t2.to != v.index && v.tunnels.none { it.to == t2.to } }
                        .map { it.copy(cost = it.cost + t.cost) }
                }.distinct()
            )
        }
)

fun System.eliminateBrokenValves() = valves.filterNotNull().filter { it.flow == 0 && it.index != 0 }.map(Valve::index).fold(this, System::eliminateValve)
fun System.eliminateValve(index: Int): System {
    val valve = valves[index]!!
    val valves = valves.map { v ->
        if(v === valve) null
        else v?.copy(tunnels = v.tunnels.flatMap { t ->
            when {
                t.to != index -> listOf(t)
                else -> valve.tunnels.filter { it.to != v.index }.map { it.copy(cost = it.cost + t.cost) }
            }
        })
    }
    return copy(valves = valves)
}

val regex = "Valve (..) has flow rate=(\\d+); tunnels? leads? to valves? (.*)".toRegex()

data class System(val names: List<String>, val valves: List<Valve?>)
data class Valve(val index: Int, val flow: Int, val tunnels: List<Tunnel>)

@JvmRecord
data class Tunnel(val to: Int, val cost: Int = 1)