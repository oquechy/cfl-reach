package cfl.reach.solver

data class Edge(val i: Int, val j: Int, val symbol: Symbol)

class Graph(forwardEdges: List<Edge>) {
    private val graph: HashMap<Int, HashMap<Symbol, HashSet<Edge>>> = HashMap()
    private val graphInv: HashMap<Int, HashMap<Symbol, HashSet<Edge>>> = HashMap()

    val edges: List<Edge>
    val nodes: Set<Int>
        get() = graph.keys

    init {
        edges = mutableListOf()
        for ((i, j, symbol) in forwardEdges) {
            val forward = Edge(i, j, symbol)
            edges.add(forward)
            add(forward)
            if (symbol is Terminal) {
                val inverted = Edge(j, i, symbol.inv())
                edges.add(inverted)
                add(inverted)
            }
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
    }
}