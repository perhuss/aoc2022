package aoc

import aoc.d01.Day01
import aoc.d02.Day02
import aoc.d03.Day03
import aoc.d04.Day04
import aoc.d05.Day05
import aoc.d06.Day06
import aoc.d07.Day07
import aoc.d08.Day08
import aoc.d09.Day09
import aoc.d10.Day10
import aoc.d11.Day11
import aoc.d12.Day12
import aoc.d13.Day13
import aoc.d14.Day14
import aoc.d15.Day15
import aoc.d16.Day16
import aoc.d17.Day17

class Day01Test : DayTest(Day01, 24000, 66186, 45000, 196804)
class Day02Test : DayTest(Day02, 15, 8392, 12, 10116)
class Day03Test : DayTest(Day03, 157, 7824, 70, 2798)
class Day04Test : DayTest(Day04, 2, 588, 4, 911)
class Day05Test : DayTest(Day05, "CMZ", "VJSFHWGFT", "MCD", "LCTQFBVZV")
class Day06Test : DayTest(Day06, 7, 1287, 19, 3716)
class Day07Test : DayTest(Day07, 95437L, 1444896L, 24933642L, 404395L)
class Day08Test : DayTest(Day08, 21, 1785, 8, 345168)
class Day09Test : DayTest(Day09, 13, 5930, 1, 2443)
class Day10Test : DayTest(Day10, 13140, 17380, day10partTwoExample, day10partTwoOutput)
class Day11Test : DayTest(Day11, 10605L, 102399L, 2713310158, 23641658401)
class Day12Test : DayTest(Day12, 31, 440, 29, 439)
class Day13Test : DayTest(Day13, 13, 5806, 140, 23600)
class Day14Test : DayTest(Day14, 24, 1003, 93, 25771)
class Day15Test : DayTest(Day15, null, 4861076, null, 10649103160102L)
class Day16Test : DayTest(Day16, 1651, 1792, 1707, 2587) // slow
class Day17Test : DayTest(Day17, 3068L, 3232L, 1514285714288L, 1585632183915L)
class Day18Test : DayTest(Day18, 64, 3448, 58, 2052)

val day10partTwoExample =
    listOf(
        "##..##..##..##..##..##..##..##..##..##..",
        "###...###...###...###...###...###...###.",
        "####....####....####....####....####....",
        "#####.....#####.....#####.....#####.....",
        "######......######......######......####",
        "#######.......#######.......#######....."
    )

val day10partTwoOutput =
    listOf(
        "####..##...##..#..#.####.###..####..##..",
        "#....#..#.#..#.#..#....#.#..#.#....#..#.",
        "###..#....#....#..#...#..#..#.###..#....",
        "#....#.##.#....#..#..#...###..#....#....",
        "#....#..#.#..#.#..#.#....#.#..#....#..#.",
        "#.....###..##...##..####.#..#.####..##.."
    )
