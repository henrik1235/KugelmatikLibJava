package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import java.nio.ByteBuffer;

/**
 * Befehl um den aktuellen Busy-Befehl des Clusters zu stoppen oder, wenn kein Busy-Befehl läuft, die Bewegung der Stepper zu stoppen.
 */
public class Stop extends Packet {
    @Override
    public PacketType getType() {
        return PacketType.Stop;
    }

    @Override
    public int getDataSize() {
        return 0;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {

    }
}
