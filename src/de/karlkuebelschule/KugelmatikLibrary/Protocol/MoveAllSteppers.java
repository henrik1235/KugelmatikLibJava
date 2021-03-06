package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;
import de.karlkuebelschule.KugelmatikLibrary.Config;

import java.nio.ByteBuffer;

/**
 * Befehl um alle Stepper des Clusters auf eine Höhe zu bringen.
 */
public class MoveAllSteppers extends Packet {
    private short height;
    private byte waitTime;

    public MoveAllSteppers(short height, byte waitTime) {
        if (height < 0 || height > Config.MaxHeight)
            throw new IllegalArgumentException("height is out of range");
        if (waitTime < 0)
            throw new IllegalArgumentException("waitTimes is out of range");

        this.height = height;
        this.waitTime = waitTime;
    }

    @Override
    public PacketType getType() {
        return PacketType.AllSteppers;
    }

    @Override
    public int getDataSize() {
        return 3;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.putShort(BinaryHelper.flipByteOrder(height));
        buffer.put(waitTime);
    }
}
