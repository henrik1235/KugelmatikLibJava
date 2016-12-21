package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;
import de.karlkuebelschule.KugelmatikLibrary.Cluster;
import de.karlkuebelschule.KugelmatikLibrary.Config;

import java.nio.ByteBuffer;

/**
 * Befehl zum Bewegen einer einzelnen Steppers auf einem Cluster.
 */
public class MoveStepper extends Packet {

    private byte x, y;
    private short height;
    private byte waitTime;

    public MoveStepper(byte x, byte y, short height, byte waitTime) {
        if (x < 0 || x >= Cluster.Width)
            throw new IllegalArgumentException("x is out of range");
        if (y < 0 || y >= Cluster.Height)
            throw new IllegalArgumentException("y is out of range");
        if (height < 0 || height > Config.MaxHeight)
            throw new IllegalArgumentException("height is out of range");

        this.x = x;
        this.y = y;
        this.height = height;
        this.waitTime = waitTime;
    }

    @Override
    public PacketType getType() {
        return PacketType.Stepper;
    }

    @Override
    public int getDataSize() {
        return 4;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.put(BinaryHelper.buildPosition(x, y));
        buffer.putShort(BinaryHelper.flipByteOrder(height));
        buffer.put(waitTime);
    }
}
