package cfl.reach.solver

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GrammarTest {
    @Test
    fun `inv inverts terminals`() {
        assertEquals(Terminal("s^-1"), Terminal("s").inv())
        assertEquals(Terminal("SYM^-1"), Terminal("SYM").inv())
        assertEquals(Terminal("s"), Terminal("s^-1").inv())
        assertEquals(Terminal("G1YpH"), Terminal("G1YpH^-1").inv())
        assertEquals(Terminal("load_123^-1"), Terminal("load_123").inv())
        assertEquals(Terminal("load_123"), Terminal("load_123^-1").inv())
    }

    @Test
    fun `productionsBySymbol returns symbol sources`() {
        val grammar = Grammar(
            NonTerminal("Start"),
            listOf(
                Production(NonTerminal("Start"), listOf(NonTerminal("A"), NonTerminal("B"), Empty)),
                Production(NonTerminal("A"), listOf(Terminal("a"))),
                Production(NonTerminal("A"), listOf(Terminal("x"))),
                Production(NonTerminal("A"), listOf(Empty)),
                Production(NonTerminal("B"), listOf(Terminal("b"))),
                Production(NonTerminal("B"), listOf(Terminal("x"))),
            )
        )
        assertTrue { grammar.productionsBySymbol(NonTerminal("Start")).isEmpty() }
        assertEquals(
            listOf(Production(NonTerminal("A"), listOf(Terminal("a")))),
            grammar.productionsBySymbol(Terminal("a"))
        )
        assertEquals(
            listOf(Production(NonTerminal("Start"), listOf(NonTerminal("A"), NonTerminal("B"), Empty))),
            grammar.productionsBySymbol(NonTerminal("B"))
        )
        assertTrue("${grammar.productionsBySymbol(Terminal("x"))}") {
            val byC = grammar.productionsBySymbol(Terminal("x"))
            byC.size == 2 && byC.containsAll(
                listOf(
                    Production(NonTerminal("A"), listOf(Terminal("x"))),
                    Production(NonTerminal("B"), listOf(Terminal("x"))),
                )
            )
        }
        assertTrue("${grammar.productionsBySymbol(Empty)}") {
            val byEmpty = grammar.productionsBySymbol(Empty)
            byEmpty.size == 2 && byEmpty.containsAll(
                listOf(
                    Production(NonTerminal("Start"), listOf(NonTerminal("A"), NonTerminal("B"), Empty)),
                    Production(NonTerminal("A"), listOf(Empty)),
                )
            )
        }
    }
}