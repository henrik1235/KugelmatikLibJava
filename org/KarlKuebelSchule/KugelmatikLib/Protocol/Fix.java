package org.KarlKuebelSchule.KugelmatikLib.Protocol;

import org.KarlKuebelSchule.KugelmatikLib.Cluster;

import java.nio.ByteBuffer;

/**
 * Created by Hendrik on 30.08.2015.
 * Der Befehl zum Neuaufwickeln einer Kugel
 */
public class Fix extends Packet {

    private byte x,y;

    public Fix(byte x, byte y){
        if(x >= Cluster.Width)
            throw new IllegalArgumentException("x is out of range");
        if(y >= Cluster.Height)
            throw new IllegalArgumentException("y is out of range");

        this.x = x;
        this.y = y;
    }

    @Override
    public PacketType getType() {
        return PacketType.Fix;
    }

    @Override
    public int getDataSize() {
        return 5;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.putInt(0xDCBA);
        buffer.put((byte)((x << 4) | y));
    }
}