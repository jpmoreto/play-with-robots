package jpm.android.messages;

/**
 * Created by jm on 29/01/17.
 *
 */

public final class BitOper {

    public static int toInt(byte msb, byte lsb) { // signed
        return (msb << 8) | (lsb & 0xFF);
    }

    public static long toLong(byte msb4, byte msb3, byte msb2, byte msb1) { // unsigned
        return (((long)(msb4 & 0xFF)) << 24) | (((long)(msb3 & 0xFF)) << 16) | (((long)(msb2 & 0xFF)) << 8) | (msb1 & 0xFF);
    }

    public static void toByte(int v,byte[] buffer) {
        buffer[0] = (byte) ((v >> 8) & 0xFF);
        buffer[1] = (byte) (v  & 0xFF);
    }

    public static void toByte(long v,byte[] buffer) {
        buffer[0] = (byte) ((v >> 24) & 0xFF);
        buffer[1] = (byte) ((v >> 16) & 0xFF);
        buffer[2] = (byte) ((v >> 8) & 0xFF);
        buffer[3] = (byte) (v  & 0xFF);
    }
}
