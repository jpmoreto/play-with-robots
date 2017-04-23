package jpm.android;

import org.junit.Test;

import static org.junit.Assert.*;
import static jpm.android.messages.BitOper.*;
/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void bitwise_testInt() throws Exception {
        int i1 = -1323;
        int i2 =  1323;

        byte[] ca = new byte[2];

        toByte(i1,ca);
        int i1_ = toInt(ca[0],ca[1]);

        toByte(i2,ca);
        int i2_ = toInt(ca[0],ca[1]);

        System.out.println("i1="+i1+"; i1_="+i1_);
        System.out.println("i2="+i2+"; v2_="+i2_);

        assertEquals(i1,i1_);
        assertEquals(i2,i2_);
    }

    @Test
    public void bitwise_testLong() throws Exception {
        long l1 = Integer.MAX_VALUE;

        byte[] ca = new byte[4];

        toByte(l1,ca);
        long l1_ = toLong(ca[0],ca[1],ca[2],ca[3]);

        System.out.println("l1="+l1+"; l1_="+l1_);

        assertEquals(l1,l1_);
    }
}