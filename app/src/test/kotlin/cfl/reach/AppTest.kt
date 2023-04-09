package cfl.reach

import kotlin.test.Test

class AppTest {
    @Test
    fun `cpp bzip`() {
        val file = AppTest::class.java.getResource(BZIP)?.path
            ?: throw RuntimeException("resource $BZIP not found")
        main(arrayOf("cpp", file))
    }

    @Test
    fun `cpp gzip`() {
        val file = AppTest::class.java.getResource(GZIP)?.path
            ?: throw RuntimeException("resource $GZIP not found")
        main(arrayOf("cpp", file))
    }
    @Test
    fun `cpp ls`() {
        val file = AppTest::class.java.getResource(LS)?.path
            ?: throw RuntimeException("resource $LS not found")
        main(arrayOf("cpp", file))
    }

    @Test
    fun `java lusearch`() {
        val file = AppTest::class.java.getResource(LuSearch)?.path
            ?: throw RuntimeException("resource $LuSearch not found")
        main(arrayOf("java", file))
    }
    @Test
    fun `java sunflow`() {
        val file = AppTest::class.java.getResource(SunFlow)?.path
            ?: throw RuntimeException("resource $SunFlow not found")
        main(arrayOf("java", file))
    }
    companion object {
        const val BZIP = "/C_points_to/bzip.csv"
        const val GZIP = "/C_points_to/gzip.csv"
        const val LS = "/C_points_to/ls.csv"
        const val LuSearch = "/Java_points_to/lusearch.csv"
        const val SunFlow = "/Java_points_to/sunflow.csv"
    }
}