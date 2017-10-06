package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.ByteBufferStringUtil;

import java.nio.ByteBuffer;

public class StartOTA extends Packet {
    private String file;

    public StartOTA(String file) {
        if (file == null)
            throw new IllegalArgumentException("file is null");

        this.file = file;
    }

    @Override
    public PacketType getType() {
        return PacketType.StartOTA;
    }

    @Override
    public int getDataSize() {
        return ByteBufferStringUtil.getLengthBytes(file);
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        ByteBufferStringUtil.writeString(buffer, file);
    }
}
