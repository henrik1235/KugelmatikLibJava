package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;
import de.karlkuebelschule.KugelmatikLibrary.Cluster;
import de.karlkuebelschule.KugelmatikLibrary.Stepper;

import java.nio.ByteBuffer;

/**
 * Befehl zum Bewegen aller Stepper mit unterschiedlichen Höhen und WaitTimes.
 */
public class MoveAllSteppersArray extends Packet {
    private short[] heights;
    private byte[] waitTimes;

    public MoveAllSteppersArray(Cluster cluster) {
        int stepperCount = Cluster.Width * Cluster.Height;

        this.heights = new short[stepperCount];
        this.waitTimes = new byte[stepperCount];

        int i = 0;
        // beide for-Schleifen müssen in der Reihenfolge übereinstimmen mit der Firmware, sonst stimmen die Positionen nicht mehr
        for (int x = 0; x < Cluster.Width; x++) {
            for (int y = 0; y < Cluster.Height; y++) {
                Stepper stepper = cluster.getStepperByPosition(x, y);
                heights[i] = stepper.getHeight();
                waitTimes[i++] = stepper.getWaitTime();
            }
        }
    }

    @Override
    public PacketType getType() {
        return PacketType.AllSteppersArray;
    }

    @Override
    public int getDataSize() {
        return heights.length * Short.BYTES + waitTimes.length * Byte.BYTES;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        for (int i = 0; i < heights.length; i++) {
            buffer.putShort(BinaryHelper.flipByteOrder(heights[i]));
            buffer.put(waitTimes[i]);
        }
    }
}
