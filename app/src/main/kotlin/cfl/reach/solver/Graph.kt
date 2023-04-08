package cfl.reach.solver

data class Edge(val i: Int, val j: Int, val symbol: Symbol)

class Graph(val edges: List<Edge>) {
    private val graph: HashMap<Int, HashMap<Symbol, HashSet<Edge>>> = HashMap()
    private val graphInv: HashMap<Int, HashMap<Symbol, HashSet<Edge>>> = HashMap()
    val nodes: Set<Int>
        get() = graph.keys

    init {
        for (e in edges) {
            add(e)
        }
    }

    operator fun contains(e: Edge): Boolean = graph[e.i]?.get(e.symbol)?.contains(e) ?: false

    fun getEdgesFrom(i:Int, symbol: Symbol) = graph[i]?.get(symbol) ?: listOf()
    fun getEdgesTo(j:Int, symbol: Symbol) = graphInv[j]?.get(symbol) ?: listOf()

    fun add(e: Edge) {
        val (i, j, symbol) = e
        graph.getOrPut(i) { HashMap() }
            .getOrPut(symbol) { HashSet() }
            .add(Edge(i, j, symbol))

        graphInv.getOrPut(j) { HashMap() }
            .getOrPut(symbol) { HashSet() }
            .add(Edge(i, j, symbol))

        if (symbol is Terminal) {
            val symbolInv = symbol.inv()
            graph.getOrPut(j) { HashMap() }
                .getOrPut(symbolInv) { HashSet() }
                .add(Edge(j, i, symbolInv))

            graphInv.getOrPut(i) { HashMap() }
                .getOrPut(symbolInv) { HashSet() }
                .add(Edge(j, i, symbolInv))
        }
    }
}