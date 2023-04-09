package cfl.reach.solver

class SolverException(message: String) : RuntimeException(message)

fun solve(graph: Graph, grammar: Grammar): List<Pair<Int, Int>> {
    val worklist = ArrayDeque<Edge>()
    worklist.addAll(graph.edges)

    for ((from, to) in grammar.productionsBySymbol(Empty)) {
        if (to != listOf(Empty)) {
            throw SolverException("bad production: $from -> $to")
        }
        for (i in graph.nodes) {
            val e = Edge(i, i, from)
            if (e !in graph) {
                graph.add(e)
                worklist.add(e)
            }
        }
    }

    while (worklist.isNotEmpty()) {
        val (i, j, s) = worklist.first()
        worklist.removeFirst()

        for ((from, to) in grammar.productionsBySymbol(s)) {
            if (to == listOf(s)) {
                val e = Edge(i, j, from)
                if (e !in graph) {
                    graph.add(e)
                    worklist.add(e)
                }
            } else if (to.size == 2 && to[0] == s) {
                for ((_, k, _) in graph.getEdgesFrom(j, to[1])) {
                    val e = Edge(i, k, from)
                    if (e !in graph) {
                        graph.add(e)
                        worklist.add(e)
                    }
                }
            } else if (to.size == 2 && to[1] == s) {
                for ((k, _, _) in graph.getEdgesTo(i, to[0])) {
                    val e = Edge(k, j, from)
                    if (e !in graph) {
                        graph.add(e)
                        worklist.add(e)
                    }
                }
            } else {
                throw SolverException("bad production: $from -> $to")
            }
        }
    }

    val reachable = mutableListOf<Pair<Int, Int>>()
    for ((i, j, _) in graph.getEdgesBySymbol(grammar.start)) {
        reachable.add(Pair(i, j))
    }
    return reachable
}