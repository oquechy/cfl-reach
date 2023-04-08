package cfl.reach.solver

import cfl.reach.App
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class SolverTest {
    @Test
    fun `solve cpp single assignment`() {
        // y = x
        //         d         d
        //   0 <------- 1 <------- 2
        //  (*x)       (x)       (&x)
        //              | a
        //         d    |    d
        //   3 <------- 4 <------- 5
        //  (*y)       (y)        (&y)
        val reach = solve(
            Graph(
                listOf(
                    // x --d-> *x
                    Edge(1, 0, Terminal("d")),
                    // &x --d-> x
                    Edge(2, 1, Terminal("d")),
                    // x --a-> y
                    Edge(1, 4, Terminal("a")),
                    // y --d-> *y
                    Edge(4, 3, Terminal("d")),
                    // &y --d-> y
                    Edge(5, 4, Terminal("d")),
                )
            ),

            App.CppGrammar
        )
        assertTrue("$reach") {
            reach.size == 6 && reach.containsAll(
                listOf(
                    Pair(0, 0), // *x --d^-1-> x --d-> *x
                    Pair(1, 1), // x --d^-1-> &x --d-> x
                    Pair(3, 3), // *y --d^-1-> y --d-> *y
                    Pair(4, 4), // y --d^-1-> &y --d-> y
                    Pair(0, 3), // *x --d^-1-> x --a-> y --d-> *y
                    Pair(3, 0), // *y --d^-1-> y --a^-1-> x --d-> *x
                )
            )
        }
    }

    @Test
    fun `solve cpp multiple assignments`() {
        // s = &t
        // r = &z
        // y = &r
        // s = r
        // x = *y
        // ... = x*
        // *s = ...
        val reach = solve(
            Graph(
                listOf(
                    // x --d-> *x
                    Edge(1, 0, Terminal("d")),
                    // &x --d-> x
                    Edge(2, 1, Terminal("d")),
                    // *y --a-> x
                    Edge(3, 1, Terminal("a")),
                    // y --d-> *y
                    Edge(4, 3, Terminal("d")),
                    // &y --d-> y
                    Edge(5, 4, Terminal("d")),
                    // &z --d-> z
                    Edge(7, 6, Terminal("d")),
                    // &z --a-> r
                    Edge(7, 8, Terminal("a")),
                    // &r --d-> r
                    Edge(9, 8, Terminal("d")),
                    // &r --a-> y
                    Edge(9, 4, Terminal("a")),
                    // r --a-> s
                    Edge(8, 11, Terminal("a")),
                    // s --d-> *s
                    Edge(11, 10, Terminal("d")),
                    // &s --d-> s
                    Edge(12, 11, Terminal("d")),
                    // &t --a-> s
                    Edge(14, 11, Terminal("a")),
                    // &t --d-> t
                    Edge(14, 13, Terminal("d")),
                )
            ),

            App.CppGrammar
        )
        assertTrue("${reach.size} $reach") {
            reach.size == 19 && reach.containsAll(
                listOf(
                    Pair(0, 10),
                    Pair(0, 6),
                    Pair(0, 0),
                    Pair(1, 1),
                    Pair(3, 8),
                    Pair(3, 3),
                    Pair(4, 4),
                    Pair(6, 0),
                    Pair(6, 10),
                    Pair(6, 6),
                    Pair(8, 3),
                    Pair(8, 8),
                    Pair(10, 6),
                    Pair(10, 0),
                    Pair(10, 13),
                    Pair(10, 10),
                    Pair(11, 11),
                    Pair(13, 10),
                    Pair(13, 13),
                )
            )
        }
    }

    @Test
    fun `solve java field assignments`() {
        // s = &t
        // r = &z
        // y = &r
        // s = r
        // x = *y
        // ... = x*
        // *s = ...
        val reach = solve(
            Graph(
                listOf(
                    // x --d-> *x
                    Edge(1, 0, Terminal("d")),
                    // &x --d-> x
                    Edge(2, 1, Terminal("d")),
                    // *y --a-> x
                    Edge(3, 1, Terminal("a")),
                    // y --d-> *y
                    Edge(4, 3, Terminal("d")),
                    // &y --d-> y
                    Edge(5, 4, Terminal("d")),
                    // &z --d-> z
                    Edge(7, 6, Terminal("d")),
                    // &z --a-> r
                    Edge(7, 8, Terminal("a")),
                    // &r --d-> r
                    Edge(9, 8, Terminal("d")),
                    // &r --a-> y
                    Edge(9, 4, Terminal("a")),
                    // r --a-> s
                    Edge(8, 11, Terminal("a")),
                    // s --d-> *s
                    Edge(11, 10, Terminal("d")),
                    // &s --d-> s
                    Edge(12, 11, Terminal("d")),
                    // &t --a-> s
                    Edge(14, 11, Terminal("a")),
                    // &t --d-> t
                    Edge(14, 13, Terminal("d")),
                )
            ),

            App.CppGrammar
        )
        assertTrue("${reach.size} $reach") {
            reach.size == 19 && reach.containsAll(
                listOf(
                    Pair(0, 10),
                    Pair(0, 6),
                    Pair(0, 0),
                    Pair(1, 1),
                    Pair(3, 8),
                    Pair(3, 3),
                    Pair(4, 4),
                    Pair(6, 0),
                    Pair(6, 10),
                    Pair(6, 6),
                    Pair(8, 3),
                    Pair(8, 8),
                    Pair(10, 6),
                    Pair(10, 0),
                    Pair(10, 13),
                    Pair(10, 10),
                    Pair(11, 11),
                    Pair(13, 10),
                    Pair(13, 13),
                )
            )
        }
    }
}