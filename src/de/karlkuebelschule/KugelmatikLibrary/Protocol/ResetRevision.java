package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import java.nio.ByteBuffer;

/**
 * Setzt die Revisionsnummer für Befehle auf dem Cluster zurück.
 */
public class ResetRevision extends Packet {
    @Override
    public PacketType getType() {
        return PacketType.ResetRevision;
    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {

    }
}
