package de.karlkuebelschule.KugelmatikLibrary;


/**
 * Eine Hilfsklasse für Binäroperationen
 */
public class BinaryHelper {
    public static byte buildPosition(int x, int y) {
        return (byte) ((x << 4) | y);
    }

    private static int getIntFromArray(long[] bytes) {
        return (int)(bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3]);
    }

    private static long getLongFromArray(long[] bytes) {
        long high = (long) bytes[0] << 54 | (long) bytes[1] << 48 | (long) bytes[2] << 40 | (long) bytes[3] << 32;
        long low = bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7];
        return high | low;
    }

    public static int flipByteOrder(int val) {
        long[] bytes = {
                val & 0xFF,
                (val >>> 8) & 0xFF,
                (val >>> 16) & 0xFF,
                (val >>> 24) & 0xFF
        };
        return getIntFromArray(bytes);
    }

    public static short flipByteOrder(short val) {
        int low = (val & 0xFF00) >>> 8;
        int high = (val & 0xFF) << 8;
        return (short)(low | high);
    }

    public static long flipByteOrder(long val) {
        long[] bytes = {
                val & 0xFF,
                (val >>> 8) & 0xFF,
                (val >>> 16) & 0xFF,
                (val >>> 24) & 0xFF,
                (val >>> 32) & 0xFF,
                (val >>> 40) & 0xFF,
                (val >>> 48) & 0xFF,
                (val >>> 56) & 0xFF
        };
        return getLongFromArray(bytes);
    }
}
