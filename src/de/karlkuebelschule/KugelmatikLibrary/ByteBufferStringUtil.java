package de.karlkuebelschule.KugelmatikLibrary;

import java.nio.ByteBuffer;

public class ByteBufferStringUtil {
    private ByteBufferStringUtil() {

    }

    public static int getLengthBytes(String string) {
        return Short.BYTES + string.length();
    }

    public static void writeString(ByteBuffer buffer, String string) {
        buffer.putShort(BinaryHelper.flipByteOrder((short)string.length()));

        for (int i = 0; i < string.length(); i++)
            buffer.put((byte)string.charAt(i));
    }
}
