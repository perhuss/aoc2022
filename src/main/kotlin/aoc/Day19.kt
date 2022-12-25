package aoc.d19

import aoc.DaySolution
import aoc.d19.Material.*
import aoc.d19.Robot.GEODE_ROBOT

object Day19 : DaySolution {
    override fun partOne(input: String) =
        input.toBlueprints().sumOf { it.quality() * it.number }
    override fun partTwo(input: String) =
        input.toBlueprints().take(3).map { it.quality(minutes = 32) }.reduce(Int::times)
}

fun Blueprint.quality(
    resources: Resources = Resources(),
    minutes: Int = 24,
    targets: List<Robot> = Robot.values().toList()
): Int =
    when {
        minutes == 0 -> resources.materials.geodes
        minutes == 1 -> resources.yield().materials.geodes
        resources.containsAll(costFor(GEODE_ROBOT)) -> quality(GEODE_ROBOT, resources, minutes, targets)
        else -> {
            val newTargets = targets.filter { resources.robots[it] < maxRobots[it] }
            newTargets.maxOf { quality(it, resources, minutes, newTargets) }
        }
    }

tailrec fun Blueprint.quality(nextRobot: Robot, resources: Resources, minutes: Int, targets: List<Robot>): Int =
    when {
        minutes == 1 -> resources.yield().materials.geodes
        resources.containsAll(costFor(nextRobot)) -> quality(build(nextRobot, resources.yield()), minutes - 1, targets)
        else -> quality(nextRobot, resources.yield(), minutes - 1, targets)
    }

fun Blueprint.build(robot: Robot, resources: Resources) =
    Resources(resources.robots + robot, resources.materials - costFor(robot))

fun String.toBlueprints() = uppercase().lines().map(String::toBlueprint)
fun String.toBlueprint() =
    split(": ").let { (id, costs) -> Blueprint(id.removePrefix("BLUEPRINT ").toInt(), costs.toRobotCosts()) }
fun String.toRobotCosts() = split(". ").map(String::toRobotCost).toMap()
fun String.toRobotCost() = split(" COSTS ").let { (r, c) -> Robot.valueOf(r.removePrefix("EACH ").replace(' ', '_')) to c.toCosts() }
fun String.toCosts() = costsRegex.findAll(this).map { it.value.split(' ').let { (q, m) -> Material.valueOf(m) to q.toInt() } }.toMaterials()
val costsRegex = "(\\d+ \\w+)".toRegex()

fun Sequence<Pair<Material, Int>>.toMaterials() = with(toMap()) {
    Materials(
        intArrayOf(
            getOrDefault(ORE, 0),
            getOrDefault(CLAY, 0),
            getOrDefault(OBSIDIAN, 0),
            getOrDefault(GEODE, 0)
        )
    )
}

data class Blueprint(val number: Int, val robotCosts: Map<Robot, Materials>) {
    val maxRobots =
        Robots(
            robotCosts.values.map(Materials::quantities)
                .plus(intArrayOf(0, 0, 0, Int.MAX_VALUE))
                .reduce(IntArray::max)
        )
    fun costFor(robot: Robot) = robotCosts.getValue(robot)
}

data class Resources(val robots: Robots = Robots(), val materials: Materials = Materials()) {
    fun yield() = copy(materials = Materials(materials.quantities + robots.quantities))
    fun containsAll(other: Materials) = materials.containsAll(other)
}

infix operator fun IntArray.plus(other: IntArray) = IntArray(4) { this[it] + other[it] }
infix operator fun IntArray.minus(other: IntArray) = IntArray(4) { this[it] - other[it] }
fun IntArray.max(other: IntArray) = IntArray(4) { this[it].coerceAtLeast(other[it]) }

@JvmInline
value class Robots(val quantities: IntArray = intArrayOf(1, 0, 0, 0)) {
    infix operator fun get(r: Robot) = quantities[r.ordinal]
    infix operator fun plus(r: Robot) =
        Robots(IntArray(4) { if(it == r.ordinal) quantities[it] + 1 else quantities[it] })
}

@JvmInline
value class Materials(val quantities: IntArray = intArrayOf(0, 0, 0, 0)) {
    fun containsAll(other: Materials) =
        quantities[0] >= other.quantities[0]
                && quantities[1] >= other.quantities[1]
                && quantities[2] >= other.quantities[2]
                && quantities[3] >= other.quantities[3]
    infix operator fun minus(other: Materials) = Materials(this.quantities - other.quantities)
    val geodes: Int get() = quantities[3]
}

enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
enum class Robot { ORE_ROBOT, CLAY_ROBOT, OBSIDIAN_ROBOT, GEODE_ROBOT }
