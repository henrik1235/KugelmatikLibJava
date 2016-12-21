package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;

import java.nio.ByteBuffer;

/**
 * Befehl eine Kugel vollständig hochzufahren, dabei wird die aktuelle Höhe der Kugel ignoriert.
 */
public class Home extends Packet {
    @Override
    public PacketType getType() {
        return PacketType.Home;
    }

    @Override
    public int getDataSize() {
        return 4;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.putInt(BinaryHelper.flipByteOrder(0xABCD));
    }
}
