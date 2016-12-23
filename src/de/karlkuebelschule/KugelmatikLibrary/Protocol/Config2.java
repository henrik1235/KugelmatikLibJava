package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.ClusterConfig;

import java.nio.ByteBuffer;

/**
 * Befehl um die Konfiguration an ein Cluster zu senden
 */
public class Config2 extends Packet {
    private ClusterConfig config;

    public Config2(ClusterConfig config) {
        this.config = config;
    }

    @Override
    public PacketType getType() {
        return PacketType.Config2;
    }

    @Override
    public int getDataSize() {
        return Short.BYTES + ClusterConfig.SIZE;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        config.write(buffer);
    }
}
