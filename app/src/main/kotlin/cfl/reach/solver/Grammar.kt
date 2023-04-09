package cfl.reach.solver

sealed interface Symbol

object Empty : Symbol

data class NonTerminal(val s: String) : Symbol

data class Terminal(val s: String) : Symbol {
    fun inv(): Symbol =
        if (s.endsWith("^-1")) {
            Terminal(s.dropLast(3))
        } else {
            Terminal("$s^-1")
        }
}

data class Production(val from: NonTerminal, val to: List<Symbol>)

class Grammar(val start: Symbol, productions: List<Production>) {
    private val symbolSources: HashMap<Symbol, MutableList<Production>> = HashMap()

    init {
        for (p in productions) {
            for (s in p.to) {
                symbolSources.getOrPut(s) { mutableListOf() }.add(p)
            }
        }
    }

    fun productionsBySymbol(s: Symbol): List<Production> = symbolSources.getOrDefault(s, listOf())
}