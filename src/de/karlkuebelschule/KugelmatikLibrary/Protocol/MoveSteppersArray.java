package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;
import de.karlkuebelschule.KugelmatikLibrary.Stepper;

import java.nio.ByteBuffer;

/**
 * Befehl zum Bewegen mehrerer Stepper auf bestimmte Höhen und WaitTimes.
 */
public class MoveSteppersArray extends Packet {
    private Item[] items;

    public MoveSteppersArray(Stepper[] steppers) {
        if (steppers == null)
            throw new IllegalArgumentException("steppers is null");
        if (steppers.length == 0 || steppers.length > Byte.MAX_VALUE)
            throw new IllegalArgumentException("steppers has invalid size (" + items.length + ")");

        this.items = new Item[steppers.length];
        for (int i = 0; i < steppers.length; i++) {
            this.items[i] = new Item(steppers[i]);
        }
    }

    @Override
    public PacketType getType() {
        return PacketType.SteppersArray;
    }

    @Override
    public int getDataSize() {
        return 1 + items.length * 4;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.put(((byte) items.length));
        for (Item item : items) {
            buffer.put(BinaryHelper.buildPosition(item.getX(), item.getY()));
            buffer.putShort(BinaryHelper.flipByteOrder(item.getHeight()));
            buffer.put(item.getWaitTime());
        }
    }

    private class Item {
        private byte x;
        private byte y;
        private short height;
        private byte waitTime;

        public Item(Stepper stepper) {
            this.x = stepper.getX();
            this.y = stepper.getY();
            this.height = stepper.getHeight();
            this.waitTime = stepper.getWaitTime();
        }

        public byte getX() {
            return x;
        }

        public byte getY() {
            return y;
        }

        public byte getWaitTime() {
            return waitTime;
        }

        public short getHeight() {
            return height;
        }
    }
}