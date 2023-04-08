package cfl.reach

import cfl.reach.solver.*
import java.io.File
import java.util.Scanner
import kotlin.system.exitProcess

object App {
    val USAGE =
        """USAGE:
        |   ./gradlew run --args='<lang> <file>'
        | 
        |To run on c++ data:
        |./gradlew run --args='cpp path/to/file.csv'
        |
        |To run on java data:
        |./gradlew run --args='java path/to/file.csv'
    """.trimMargin()

    val CppGrammar = Grammar(
        NonTerminal("M"),
        listOf(
            Production(NonTerminal("M"), listOf(Terminal("d^-1"), NonTerminal("Vd"))),
            Production(NonTerminal("Vd"), listOf(NonTerminal("V"), Terminal("d"))),
            Production(NonTerminal("V"), listOf(NonTerminal("(M?a^-1)*"), NonTerminal("M?(aM?)*"))),
            Production(NonTerminal("(M?a^-1)*"), listOf(Empty)),
            Production(NonTerminal("(M?a^-1)*"), listOf(NonTerminal("M?a^-1"), NonTerminal("(M?a^-1)*"))),
            Production(NonTerminal("M?a^-1"), listOf(NonTerminal("M?"), Terminal("a^-1"))),
            Production(NonTerminal("M?"), listOf(Empty)),
            Production(NonTerminal("M?"), listOf(NonTerminal("M"))),
            Production(NonTerminal("M?(aM?)*"), listOf(NonTerminal("M?"), NonTerminal("(aM?)*"))),
            Production(NonTerminal("(aM?)*"), listOf(Empty)),
            Production(NonTerminal("(aM?)*"), listOf(NonTerminal("aM?"), NonTerminal("(aM?)*"))),
            Production(NonTerminal("aM?"), listOf(Terminal("a"), NonTerminal("M?"))),
        )
    )
}

fun main(args: Array<String>) {
    if (args.size != 2) {
        System.err.println(App.USAGE)
        exitProcess(1)
    }
    if (args[0] !in listOf("cpp", "java")) {
        System.err.println(App.USAGE)
        exitProcess(1)
    }

    val solution = mutableListOf<Pair<Int, Int>>()
    when (args[0]) {
        "cpp" -> {
            val edges = readEdges(args[1])
            solution.addAll(solve(Graph(edges), App.CppGrammar))
        }

        "java" -> {
            solution.addAll(solve(Graph(readEdges(args[1])), Grammar(Empty, listOf())))
        }

        else -> {
            System.err.println(App.USAGE)
            exitProcess(1)
        }
    }

    for ((i, j) in solution) {
        println("$i $j")
    }
}

fun readEdges(file: String): List<Edge> {
    val edges = ArrayList<Edge>()
    Scanner(File(file).bufferedReader()).use {
        while (it.hasNextInt()) {
            val i = it.nextInt()
            val j = it.nextInt()
            val s = it.next()
            val e = Edge(i, j, Terminal(s))
            edges.add(e)
        }
    }
    return edges
}
