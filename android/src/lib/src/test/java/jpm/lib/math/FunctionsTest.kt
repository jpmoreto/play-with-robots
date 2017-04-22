package jpm.lib.math

import junit.framework.Assert.assertTrue
import org.junit.Test

/**
 * Created by jm on 11/03/17.
 *
 */
class FunctionsTest {
    @Test
    fun convexHullTest() {

        val testPoints = arrayOf(
                DoubleVector2D(9230.0, 13137.0),
                DoubleVector2D(4096.0, 24064.0),
                DoubleVector2D(8192.0, 26112.0),
                DoubleVector2D(22016.0, 9344.0),
                DoubleVector2D(4440.0, 8028.0),
                DoubleVector2D(6505.0, 31422.0),
                DoubleVector2D(28462.0, 32343.0),
                DoubleVector2D(17152.0, 19200.0),
                DoubleVector2D(9561.0, 11599.0),
                DoubleVector2D(4096.0, 20992.0),
                DoubleVector2D(21538.0, 2430.0),
                DoubleVector2D(21903.0, 23677.0),
                DoubleVector2D(17152.0, 16128.0),
                DoubleVector2D(7168.0, 25088.0),
                DoubleVector2D(10162.0, 18638.0),
                DoubleVector2D(822.0, 32301.0),
                DoubleVector2D(16128.0, 12032.0),
                DoubleVector2D(18989.0, 3797.0),
                DoubleVector2D(8192.0, 28160.0),
                DoubleVector2D(16128.0, 20224.0),
                DoubleVector2D(14080.0, 20224.0),
                DoubleVector2D(26112.0, 7296.0),
                DoubleVector2D(20367.0, 20436.0),
                DoubleVector2D(7486.0, 422.0),
                DoubleVector2D(17835.0, 2689.0),
                DoubleVector2D(22016.0, 3200.0),
                DoubleVector2D(22016.0, 5248.0),
                DoubleVector2D(24650.0, 16886.0),
                DoubleVector2D(15104.0, 20224.0),
                DoubleVector2D(25866.0, 4204.0),
                DoubleVector2D(13056.0, 15104.0),
                DoubleVector2D(13662.0, 10301.0),
                DoubleVector2D(17152.0, 20224.0),
                DoubleVector2D(15104.0, 12032.0),
                DoubleVector2D(6144.0, 20992.0),
                DoubleVector2D(26112.0, 3200.0),
                DoubleVector2D(6144.0, 29184.0),
                DoubleVector2D(13056.0, 12032.0),
                DoubleVector2D(8128.0, 20992.0),
                DoubleVector2D(5076.0, 19172.0),
                DoubleVector2D(17152.0, 17152.0),
                DoubleVector2D(823.0, 15895.0),
                DoubleVector2D(25216.0, 3200.0),
                DoubleVector2D(6071.0, 29161.0),
                DoubleVector2D(5120.0, 20992.0),
                DoubleVector2D(10324.0, 22176.0),
                DoubleVector2D(29900.0, 9390.0),
                DoubleVector2D(27424.0, 7945.0),
                DoubleVector2D(4096.0, 23040.0),
                DoubleVector2D(12831.0, 27971.0),
                DoubleVector2D(29860.0, 12437.0),
                DoubleVector2D(28668.0, 2061.0),
                DoubleVector2D(1429.0, 12561.0),
                DoubleVector2D(29413.0, 596.0),
                DoubleVector2D(17152.0, 18176.0),
                DoubleVector2D(8192.0, 27136.0),
                DoubleVector2D(5120.0, 29184.0),
                DoubleVector2D(22016.0, 11392.0),
                DoubleVector2D(1444.0, 10362.0),
                DoubleVector2D(32011.0, 3140.0),
                DoubleVector2D(15731.0, 32661.0),
                DoubleVector2D(26112.0, 4224.0),
                DoubleVector2D(13120.0, 20224.0),
                DoubleVector2D(30950.0, 2616.0),
                DoubleVector2D(4096.0, 22016.0),
                DoubleVector2D(4096.0, 25088.0),
                DoubleVector2D(24064.0, 3200.0),
                DoubleVector2D(26112.0, 5248.0),
                DoubleVector2D(4862.0, 30650.0),
                DoubleVector2D(5570.0, 8885.0),
                DoubleVector2D(21784.0, 18853.0),
                DoubleVector2D(23164.0, 32371.0),
                DoubleVector2D(4160.0, 29184.0),
                DoubleVector2D(13056.0, 13056.0),
                DoubleVector2D(8192.0, 29184.0),
                DoubleVector2D(23040.0, 7296.0),
                DoubleVector2D(5120.0, 25088.0),
                DoubleVector2D(22016.0, 7296.0),
                DoubleVector2D(7168.0, 29184.0),
                DoubleVector2D(25216.0, 7296.0),
                DoubleVector2D(23040.0, 3200.0),
                DoubleVector2D(4718.0, 4451.0),
                DoubleVector2D(14080.0, 16128.0),
                DoubleVector2D(7168.0, 20992.0),
                DoubleVector2D(19546.0, 17728.0),
                DoubleVector2D(13056.0, 16128.0),
                DoubleVector2D(17947.0, 17017.0),
                DoubleVector2D(26112.0, 6272.0),
                DoubleVector2D(20658.0, 1204.0),
                DoubleVector2D(23553.0, 13965.0),
                DoubleVector2D(13056.0, 14080.0),
                DoubleVector2D(14080.0, 12032.0),
                DoubleVector2D(24064.0, 7296.0),
                DoubleVector2D(21377.0, 26361.0),
                DoubleVector2D(17088.0, 12032.0),
                DoubleVector2D(16128.0, 16128.0),
                DoubleVector2D(30875.0, 28560.0),
                DoubleVector2D(2542.0, 26201.0),
                DoubleVector2D(8192.0, 25088.0),
                DoubleVector2D(11444.0, 16973.0))


        println("test points:")
        for (p in testPoints) {
            println("  $p")
        }

        val convexHull = convexHull(testPoints)

        println("convexHull:")
        for (p in convexHull) {
            println("  $p")
        }
        assertTrue(isConvex(convexHull))
    }

    @Test
    fun rectanguleIntersectionTest() {

        val r = Rectangle(DoubleVector2D(0.0, 0.0), DoubleVector2D(20.0, 10.0))

        val r_u = Rectangle(DoubleVector2D(10.0, 5.0), DoubleVector2D(30.0, 15.0))
        val int_r__r_u = intersection(r, r_u)

        println("intersection($r,$r_u) = $int_r__r_u")
        assertTrue(int_r__r_u == Rectangle(DoubleVector2D(10.0, 5.0), DoubleVector2D(20.0, 10.0)))

        val r_d = Rectangle(DoubleVector2D(10.0, -5.0), DoubleVector2D(30.0, 5.0))
        val int_r__r_d = intersection(r, r_d)

        println("intersection($r,$r_d) = $int_r__r_d")
        assertTrue(int_r__r_d == Rectangle(DoubleVector2D(10.0, 0.0), DoubleVector2D(20.0, 5.0)))

        val l_u = Rectangle(DoubleVector2D(-10.0, 5.0), DoubleVector2D(10.0, 15.0))
        val int_r__l_u = intersection(r, l_u)

        println("intersection($r,$l_u) = $int_r__l_u")
        assertTrue(int_r__l_u == Rectangle(DoubleVector2D(0.0, 5.0), DoubleVector2D(10.0, 10.0)))

        val l_d = Rectangle(DoubleVector2D(-10.0, -5.0), DoubleVector2D(10.0, 5.0))
        val int_r__l_d = intersection(r, l_d)

        println("intersection($r,$l_d) = $int_r__l_d")
        assertTrue(int_r__l_d == Rectangle(DoubleVector2D(0.0, 0.0), DoubleVector2D(10.0, 5.0)))

        val l = Rectangle(DoubleVector2D(-10.0, 0.0), DoubleVector2D(-1.0, 20.0))
        val int_r__l = intersection(r, l)

        println("intersection($r,$l) = $int_r__l")
        assertTrue(int_r__l == null)

        val r_ = Rectangle(DoubleVector2D(30.0, 0.0), DoubleVector2D(40.0, 20.0))
        val int_r__r = intersection(r, r_)

        println("intersection($r,$r_) = $int_r__r")
        assertTrue(int_r__r == null)

        val u = Rectangle(DoubleVector2D(0.0, 21.0), DoubleVector2D(20.0, 40.0))
        val int_r__u = intersection(r, u)

        println("intersection($r,$u) = $int_r__u")
        assertTrue(int_r__u == null)

        val d = Rectangle(DoubleVector2D(0.0, -11.0), DoubleVector2D(20.0, -1.0))
        val int_r__d = intersection(r, d)

        println("intersection($r,$d) = $int_r__d")
        assertTrue(int_r__d == null)

        val int_r__i = intersection(r, r)

        println("intersection($r,$r) = $int_r__i")
        assertTrue(int_r__i == r)
    }

    @Test
    fun lineIntersectionTest() {
        val lh = LineSegment(DoubleVector2D(0.0,0.0), DoubleVector2D(10.0,0.0))

        val lv = LineSegment(DoubleVector2D(5.0,-5.0), DoubleVector2D(5.0,5.0))

        val int_lh_lv = intersection(lh, lv)

        println("intersection($lh,$lv) = $int_lh_lv")
        assertTrue(int_lh_lv!!.p1.nearEquals(DoubleVector2D(5.0,0.0),1E-30))
        assertTrue(int_lh_lv.p2.nearEquals(DoubleVector2D(5.0,0.0),1E-30))

        val lh1 = LineSegment(DoubleVector2D(5.0,0.0), DoubleVector2D(20.0,0.0))

        val int_lh_lh1 = intersection(lh, lh1)

        println("intersection($lh,$lh1) = $int_lh_lh1")
        assertTrue(int_lh_lh1!!.p2.nearEquals(DoubleVector2D(5.0,0.0),1E-30))
        assertTrue(int_lh_lh1.p1.nearEquals(DoubleVector2D(10.0,0.0),1E-30))
    }
}