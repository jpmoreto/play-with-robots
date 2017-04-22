package jpm.android.robot

import org.junit.Test
import org.junit.Assert.*

/**
 * Created by jm on 06/03/17.
 *
 */
class PoseTst {
    @Test
    fun changeCoordinates() {
        val p = Pose(0, 100.0, 200.0, Math.PI / 4)
        val pg = toGlobalCoordinates(p)
        val pl = toLocalCoordinates(pg)
        println("p=$p;\npg=$pg;\npl=$pl")
        assertTrue( -1E-10 <= (pl.x - p.x) && (pl.x - p.x)  <= 1E-10)
        assertTrue( -1E-10 <= (pl.y - p.y) && (pl.y - p.y)  <= 1E-10)
        assertTrue(pl.angle == p.angle)
        assertTrue(pl.time == p.time)
    }
}