package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import java.nio.ByteBuffer;

/**
 * Befehl um die Konfiguration vom Cluster abzurufen.
 */
public class PacketInfo extends Packet {
    private boolean requestConfig2;

    public PacketInfo(boolean requestConfig2) {
        this.requestConfig2 = requestConfig2;
    }

    @Override
    public PacketType getType() {
        return PacketType.Info;
    }

    @Override
    public int getDataSize() {
        return 1;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.put((byte)(requestConfig2 ? 1 : 0));
    }
}
