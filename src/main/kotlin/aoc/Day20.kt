package aoc

object Day20 : DaySolution {
    override fun partOne(input: String) = input.toEncryptedFile().mix().coords()
    override fun partTwo(input: String) =
        generateSequence(input.toEncryptedFile().applyKey(), EncryptedFile::mix).drop(10).first().coords()
}

fun EncryptedFile.applyKey() = EncryptedFile(nodes.map { it.copy(number = it.number * 811589153) })

fun EncryptedFile.coords() = with(nodes) {
    val index = indexOfFirst { it.number == 0L }
    listOf(1000, 2000, 3000)
        .map { (index + it) % size }
        .map(::get)
        .sumOf(Node::number)
}

fun EncryptedFile.mix() = with(nodes.toMutableList()) {
    indices.forEach { originalIndex ->
        val index = indexOfFirst { it.originalIndex == originalIndex }
        val node = removeAt(index)
        val newIndex = (index + node.number % size + size) % size
        add(newIndex.toInt(), node)
    }
    EncryptedFile(this)
}

fun String.toEncryptedFile() = EncryptedFile(lines().map(String::toLong).mapIndexed(::Node))

data class EncryptedFile(val nodes: List<Node>)
data class Node(val originalIndex: Int, val number: Long)