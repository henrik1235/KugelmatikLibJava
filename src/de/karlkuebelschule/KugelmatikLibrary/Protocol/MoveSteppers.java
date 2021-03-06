package de.karlkuebelschule.KugelmatikLibrary.Protocol;

import de.karlkuebelschule.KugelmatikLibrary.BinaryHelper;
import de.karlkuebelschule.KugelmatikLibrary.Config;
import de.karlkuebelschule.KugelmatikLibrary.Stepper;

import java.nio.ByteBuffer;

/**
 * Befehl um mehrere Stepper auf eine Höhe zu bringen.
 */
public class MoveSteppers extends Packet {
    private Item[] items;
    private short height;
    private byte waitTime;

    public MoveSteppers(Stepper[] steppers, short height, byte waitTime) {
        if (steppers == null)
            throw new IllegalArgumentException("steppers is null");
        if (steppers.length == 0)
            throw new IllegalArgumentException("steppers is empty");
        if (height < 0 || height > Config.MaxHeight)
            throw new IllegalArgumentException("height is out of range");
        if (waitTime < 0)
            throw new IllegalArgumentException("waitTime is out of range");

        this.height = height;
        this.waitTime = waitTime;

        this.items = new Item[steppers.length];
        for (int i = 0; i < steppers.length; i++) {
            this.items[i] = new Item(steppers[i]);
        }
    }

    @Override
    public PacketType getType() {
        return PacketType.Steppers;
    }

    @Override
    public int getDataSize() {
        return 4 + items.length;
    }

    @Override
    protected void allocateBuffer(ByteBuffer buffer) {
        buffer.put((byte) items.length);
        buffer.putShort(BinaryHelper.flipByteOrder(height));
        buffer.put(waitTime);
        for (Item item : items)
            buffer.put(BinaryHelper.buildPosition(item.getX(), item.getY()));
    }

    private class Item {
        private byte x, y;

        public Item(Stepper stepper) {
            this.x = stepper.getX();
            this.y = stepper.getY();
        }

        public byte getY() {
            return y;
        }

        public byte getX() {
            return x;
        }
    }
}
