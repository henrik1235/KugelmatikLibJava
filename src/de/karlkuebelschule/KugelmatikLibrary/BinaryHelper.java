package de.karlkuebelschule.KugelmatikLibrary;


/**
 * Eine Hilfsklasse für Binäroperationen
 */
public class BinaryHelper {
    public static byte buildPosition(int x, int y) {
        return (byte) ((x << 4) | y);
    }

    private static int getIntFromByteArray(byte[] bytes) {
        return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
    }

    private static long getLongFromByteArray(byte[] bytes) {
        long high = (long) bytes[0] << 54 | (long) bytes[1] << 48 | (long) bytes[2] << 40 | (long) bytes[3] << 32;
        long low = bytes[4] << 24 | bytes[5] << 16 | bytes[6] << 8 | bytes[7];
        return high | low;
    }

    public static int flipByteOrder(int val) {
        byte[] bytes = {
                ((byte) val),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24),
        };
        return getIntFromByteArray(bytes);
    }

    public static short flipByteOrder(short val) {
        return (short) ((val & 0xFF) << 8 | (val) >> 8);
    }

    public static long flipByteOrder(long val) {
        byte[] bytes = {
                ((byte) val),
                (byte) (val >> 8),
                (byte) (val >> 16),
                (byte) (val >> 24),
                (byte) (val >> 32),
                (byte) (val >> 40),
                (byte) (val >> 48),
                (byte) (val >> 56),
        };
        return getLongFromByteArray(bytes);
    }
}
