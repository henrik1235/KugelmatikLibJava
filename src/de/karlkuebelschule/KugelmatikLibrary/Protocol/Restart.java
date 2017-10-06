package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import java.nio.ByteBuffer;

public class Restart extends Packet {
    @Override
    public PacketType getType() {
        return PacketType.Restart;
    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {

    }
}
