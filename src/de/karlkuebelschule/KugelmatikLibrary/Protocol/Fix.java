package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;
import de.karlkuebelschule.KugelmatikLibrary.Cluster;

import java.nio.ByteBuffer;

/**
 * Der Fix Befehl zum vollständigen Herrunterfahren eines Steppers, dabei wird die maximale Anzahl an Schritten ignoriert.
 */
public class Fix extends Packet {
    private byte x, y;

    public Fix(byte x, byte y) {
        if (x >= Cluster.Width)
            throw new IllegalArgumentException("x is out of range");
        if (y >= Cluster.Height)
            throw new IllegalArgumentException("y is out of range");

        this.x = x;
        this.y = y;
    }

    @Override
    public PacketType getType() {
        return PacketType.Fix;
    }

    @Override
    public int getDataSize() {
        return 5;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.putInt(BinaryHelper.flipByteOrder(0xDCBA));
        buffer.put(BinaryHelper.buildPosition(x, y));
    }
}
