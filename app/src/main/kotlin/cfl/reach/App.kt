package cfl.reach

import cfl.reach.solver.*
import java.io.File
import java.util.Scanner
import kotlin.system.exitProcess

object App {
    val USAGE = """USAGE:
        |   ./gradlew run --args='<lang> <file>'
        | 
        |To run on c++ data:
        |./gradlew run --args='cpp path/to/file.csv'
        |
        |To run on java data:
        |./gradlew run --args='java path/to/file.csv'
    """.trimMargin()

    val CppGrammar = Grammar(
        NonTerminal("M"), listOf(
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

    val JavaGrammar = listOf(
        Production(NonTerminal("A"), listOf(NonTerminal("PointsTo"), NonTerminal("FlowsTo"))),
        Production(
            NonTerminal("PointsTo"),
            listOf(NonTerminal("(assign | LAS)*"), Terminal("alloc"))
        ),
        Production(NonTerminal("(assign | LAS)*"), listOf(Empty)),
        Production(
            NonTerminal("(assign | LAS)*"),
            listOf(NonTerminal("(assign | LAS)"), NonTerminal("(assign | LAS)*"))
        ),
        Production(NonTerminal("(assign | LAS)"), listOf(Terminal("assign"))),
        Production(NonTerminal("(assign | LAS)"), listOf(NonTerminal("LAS"))),
        Production(
            NonTerminal("FlowsTo"),
            listOf(Terminal("alloc^-1"), NonTerminal("(assign^-1 | S^-1AL^-1)*"))
        ),
        Production(NonTerminal("(assign^-1 | S^-1AL^-1)*"), listOf(Empty)),
        Production(
            NonTerminal("(assign^-1 | S^-1AL^-1)*"),
            listOf(
                NonTerminal("(assign^-1 | S^-1AL^-1)"),
                NonTerminal("(assign^-1 | S^-1AL^-1)*")
            )
        ),
        Production(NonTerminal("(assign^-1 | S^-1AL^-1)"), listOf(Terminal("assign^-1"))),
        Production(
            NonTerminal("(assign^-1 | S^-1AL^-1)"),
            listOf(NonTerminal("S^-1AL^-1"))
        ),
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

    when (args[0]) {
        "cpp" -> {
            val edges = readEdges(args[1])
            val aliases = solve(Graph(edges), App.CppGrammar)
            println("Alias size: ${aliases.size}")
            println("Alias set:")
            for ((i, j) in aliases) {
                println("$i $j")
            }
        }

        "java" -> {
            val edges = readEdges(args[1])
            val productions = buildFieldProductions(edges)
            val graph = Graph(edges)
            val aliases = solve(graph, Grammar(NonTerminal("A"), productions))
            println("PointsTo size: ${graph.countEdgesBySymbol(NonTerminal("PointsTo"))}")
            println("Alias size: ${aliases.size}")
            println("Alias set:")
            for ((i, j) in aliases) {
                println("$i $j")
            }
        }

        else -> {
            System.err.println(App.USAGE)
            exitProcess(1)
        }
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

fun buildFieldProductions(edges: List<Edge>): List<Production> {
    val loadedFields = HashSet<String>()
    val storedFields = HashSet<String>()
    val productions = App.JavaGrammar.toMutableList()
    for ((_, _, s) in edges) {
        if (s is Terminal) {
            if (s.s.startsWith("load_")) {
                loadedFields.add(s.s.drop(5))
            } else if (s.s.startsWith("store_")) {
                storedFields.add(s.s.drop(6))
            }
        }
    }
    for (f in loadedFields) {
        if (f in storedFields) {
            productions.addAll(
                listOf(
                    Production(
                        NonTerminal("LAS"),
                        listOf(Terminal("load_$f"), NonTerminal("A store_$f"))
                    ),
                    Production(
                        NonTerminal("A store_$f"),
                        listOf(NonTerminal("A"), Terminal("store_$f"))
                    ),
                    Production(
                        NonTerminal("S^-1AL^-1"),
                        listOf(Terminal("store_$f^-1"), NonTerminal("A load_$f^-1"))
                    ),
                    Production(
                        NonTerminal("A load_$f^-1"),
                        listOf(NonTerminal("A"), Terminal("load_$f^-1"))
                    ),
                )
            )
        }
    }
    return productions
}