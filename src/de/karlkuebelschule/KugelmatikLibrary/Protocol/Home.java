package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;

import java.nio.ByteBuffer;

/**
 * Stellt den Home Befehl dar. Bewegt alle Stepper eines Clusters vollständig nach oben. Die derzeitige Höhe wird ignoriert.
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
