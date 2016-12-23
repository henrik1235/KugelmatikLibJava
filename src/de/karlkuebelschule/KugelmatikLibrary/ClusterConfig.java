package de.karlkuebelschule.KugelmatikLibrary;


import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Stellt die Konfiguration eines Clusters dar.
 */
public class ClusterConfig {
    /**
     * Größe von ClusterConfig in Bytes.
     */
    public static final int SIZE = 24;

    private StepMode stepMode;
    private BrakeMode brakeMode;

    private int tickTime;
    private int homeTime;
    private int fixTime;

    private int maxSteps;
    private int homeSteps;
    private int fixSteps;

    private int brakeTicks;
    private int minStepDelta;

    /**
     * Initialisert eine ClusterConfig mit Standardwerten.
     */
    public ClusterConfig() {
        stepMode = StepMode.HalfStep;
        brakeMode = BrakeMode.Smart;

        tickTime = 3500;
        homeTime = 3500;
        fixTime = 3500;

        maxSteps = 8000;
        homeSteps = 8000;
        fixSteps = 8000;

        brakeTicks = 10000;

        minStepDelta = 10;
    }

    public ClusterConfig(StepMode stepMode, BrakeMode brakeMode, int tickTime, int homeTime, int fixTime, int maxSteps, int homeSteps, int fixSteps, int brakeTicks, int minStepDelta) {
        this.stepMode = stepMode;
        this.brakeMode = brakeMode;
        this.tickTime = tickTime;
        this.homeTime = homeTime;
        this.fixTime = fixTime;
        this.maxSteps = maxSteps;
        this.homeSteps = homeSteps;
        this.fixSteps = fixSteps;
        this.brakeTicks = brakeTicks;
        this.minStepDelta = minStepDelta;
    }

    public ClusterConfig(DataInputStream stream) throws IOException {
        this();

        int size = BinaryHelper.flipByteOrder(stream.readShort());

        if (size != SIZE)
            throw new IOException("Remote size does not match ClusterConfig size");

        stepMode = StepMode.values()[stream.readUnsignedByte() - 1];
        brakeMode = BrakeMode.values()[stream.readUnsignedByte()];

        tickTime = BinaryHelper.flipByteOrder(stream.readInt());
        homeTime = BinaryHelper.flipByteOrder(stream.readInt());
        fixTime = BinaryHelper.flipByteOrder(stream.readInt());

        maxSteps = BinaryHelper.flipByteOrder(stream.readShort());
        homeSteps = BinaryHelper.flipByteOrder(stream.readShort());
        fixSteps = BinaryHelper.flipByteOrder(stream.readShort());

        brakeTicks = BinaryHelper.flipByteOrder(stream.readShort());
        minStepDelta = BinaryHelper.flipByteOrder(stream.readShort());
    }

    public void write(ByteBuffer buffer) {
        buffer.putShort((short)SIZE);

        buffer.put(stepMode.getByteValue());
        buffer.put(brakeMode.getByteValue());

        buffer.putInt(BinaryHelper.flipByteOrder(tickTime));
        buffer.putInt(BinaryHelper.flipByteOrder(homeTime));
        buffer.putInt(BinaryHelper.flipByteOrder(fixTime));

        buffer.putShort(BinaryHelper.flipByteOrder((short)maxSteps));
        buffer.putShort(BinaryHelper.flipByteOrder((short)homeSteps));
        buffer.putShort(BinaryHelper.flipByteOrder((short)fixSteps));

        buffer.putShort(BinaryHelper.flipByteOrder((short)brakeTicks));
        buffer.putShort(BinaryHelper.flipByteOrder((short)minStepDelta));
    }

    public StepMode getStepMode() {
        return stepMode;
    }

    public void setStepMode(StepMode stepMode) {
        this.stepMode = stepMode;
    }

    public BrakeMode getBrakeMode() {
        return brakeMode;
    }

    public void setBrakeMode(BrakeMode brakeMode) {
        this.brakeMode = brakeMode;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public int getHomeTime() {
        return homeTime;
    }

    public void setHomeTime(int homeTime) {
        this.homeTime = homeTime;
    }

    public int getFixTime() {
        return fixTime;
    }

    public void setFixTime(int fixTime) {
        this.fixTime = fixTime;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public void setMaxSteps(int maxSteps) {
        this.maxSteps = maxSteps;
    }

    public int getHomeSteps() {
        return homeSteps;
    }

    public void setHomeSteps(int homeSteps) {
        this.homeSteps = homeSteps;
    }

    public int getFixSteps() {
        return fixSteps;
    }

    public void setFixSteps(int fixSteps) {
        this.fixSteps = fixSteps;
    }

    public int getBrakeTicks() {
        return brakeTicks;
    }

    public void setBrakeTicks(int brakeTicks) {
        this.brakeTicks = brakeTicks;
    }

    public int getMinStepDelta() {
        return minStepDelta;
    }

    public void setMinStepDelta(int minStepDelta) {
        this.minStepDelta = minStepDelta;
    }
}
