package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import java.nio.ByteBuffer;

public class ClearError extends Packet {
    @Override
    public PacketType getType() {
        return PacketType.ClearError;
    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {

    }
}
