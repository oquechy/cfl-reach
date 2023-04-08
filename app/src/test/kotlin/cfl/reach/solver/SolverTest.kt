package cfl.reach.solver

import cfl.reach.App
import cfl.reach.buildFieldProductions
import kotlin.test.Test
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
        assertTrue("reachable pairs $reach") {
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
        assertTrue("size ${reach.size} reachable pairs $reach") {
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
        // x = new Obj()
        // z = new Obj()
        // w = x
        // y = x
        // y.f = z
        // v = w.f
        val edges = listOf(
            // o1 --alloc-> x
            Edge(0, 1, Terminal("alloc")),
            // x --assign-> w
            Edge(1, 2, Terminal("assign")),
            // w --load_f-> v
            Edge(2, 3, Terminal("load_1")),
            // x --assign-> y
            Edge(1, 4, Terminal("assign")),
            // z --store_f-> y
            Edge(5, 4, Terminal("store_1")),
            // o2 --alloc-> z
            Edge(6, 5, Terminal("alloc")),
        )
        val grammar = Grammar(NonTerminal("A"), buildFieldProductions(edges))
        val reach = solve(Graph(edges), grammar)
        assertTrue("size ${reach.size} reachable pairs $reach") {
            reach.size == 13 && reach.containsAll(
                listOf(
                    Pair(1, 1),
                    Pair(2, 2),
                    Pair(4, 4),
                    Pair(2, 4),
                    Pair(4, 2),
                    Pair(1, 2),
                    Pair(2, 1),
                    Pair(1, 4),
                    Pair(4, 1),
                    Pair(3, 3),
                    Pair(5, 5),
                    Pair(3, 5),
                    Pair(5, 3),
                )
            )
        }
    }
}