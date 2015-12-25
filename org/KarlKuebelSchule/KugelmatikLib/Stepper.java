package org.KarlKuebelSchule.KugelmatikLib;

import com.sun.istack.internal.NotNull;

/**
 * Created by Hendrik on 29.08.2015.
 * Repräsentiert einen Schrittmotor eines Clusters
 */
public class Stepper {
    private byte x;
    private byte y;

    private int absoluteX;
    private int absoluteY;

    private Cluster cluster;

    private short height = 0;
    private byte waitTime = 0;
    private boolean dataChanged = false;

    private IHeightChangedHandler heightChangedHandler;

    public Stepper(@NotNull Cluster cluster, byte x, byte y) {
        if (x < 0 || x >= Cluster.Width)
            throw new IllegalArgumentException("x");
        if (y < 0 || y >= Cluster.Height)
            throw new IllegalArgumentException("y");

        this.cluster = cluster;
        this.x = x;
        this.y = y;

        absoluteX = cluster.getX() * Cluster.Width + x;
        absoluteY = cluster.getY() * Cluster.Height + y;
    }

    /**
     * Setzt die Werte die Kugel zurück, zum Beispiel nach einem Home-Befehl
     */
    public void reset() {
        setHeight((short) 0);
        dataChanged = false;
    }

    /**
     * Bewegt die Kugel in eine bestimmte Höhe
     *
     * @param height Die Höhe der Kugel
     */
    public synchronized void moveTo(short height) {
        moveTo(height, Config.DefaultWaitTime);
    }

    /**
     * Bewegt die Kugel in eine bestimmte Höhe zu einer bestimmten Zeit
     *
     * @param height   Die Höhe der Kugel
     * @param waitTime Die Zeit die gewartet werden soll
     */
    public synchronized void moveTo(short height, byte waitTime) {
        if (height < 0 || height > Config.MaxHeight)
            throw new IllegalArgumentException("height is out of range");
        if (waitTime < 0)
            throw new IllegalArgumentException("waitTime is out of range");

        if (this.height == height && this.waitTime == waitTime)
            return;

        setHeight(height);
        this.waitTime = waitTime;
        dataChanged = true;
    }

    /**
     * Setzte die Kugel auf Home.
     */
    public void sendHome() {
        cluster.sendHome(x, y);
    }

    /**
     * Wickelt die Kugel auf und wieder ab.
     */
    public void sendFix() {
        cluster.sendFix(x, y);
    }

    /**
     * Setzt den HeightChangedEventHandler
     *
     * @param heightChangedHandler Der HeightChangedEventHandler
     */
    public void setHeightChangedHandler(IHeightChangedHandler heightChangedHandler) {
        this.heightChangedHandler = heightChangedHandler;
    }

    /**
     * Gibt die WaitTime zurück.
     */
    public byte getWaitTime() {
        return waitTime;
    }

    /**
     * Setzt die waitTime der Kugel, sendet jedoch keinen Befehl an das Cluster.
     *
     * @param waitTime Die waitTime der Kugel
     */
    public void setWaitTime(byte waitTime) {
        this.waitTime = waitTime;
    }

    /**
     * Gibt zurück ob sich die Höhe der Kugel seit dem letzten Senden verändert hat.
     */
    public boolean hasDataChanged() {
        return dataChanged;
    }

    /**
     * Legt fest, dass die Änderungen an das Cluster gesendet wurden.
     */
    public void updated() {
        dataChanged = false;
    }

    /**
     * Gibt die Höhe der Kugel zurück.
     */
    public short getHeight() {
        return height;
    }

    /**
     * Setzt die Höhe der Kugel, sendet jedoch keinen Befehl an das Cluster.
     *
     * @param height Die Höhe der Kugel
     */
    public void setHeight(short height) {
        if (this.height != height) {
            this.height = height;
            if (heightChangedHandler != null)
                heightChangedHandler.onHeightChanged(this);
        }
    }

    /**
     * Gibt die x-Koordinate des Motors in einem Cluster zurück
     */
    public byte getX() {
        return x;
    }

    /**
     * Gibt die y-Koordinate des Motors in einem Cluster zurück.
     */
    public byte getY() {
        return y;
    }

    /**
     * Gibt die absolute x-Koordinate der Postion des Steppers zurück
     */
    public int getAbsoluteX() {
        return absoluteX;
    }

    /**
     * Gibt die absolute y-Koordinate der Postion des Steppers zurück
     */
    public int getAbsoluteY() {
        return absoluteY;
    }

    /**
     * Gibt das zugeh�rige Cluster zurück.
     */
    public Cluster getCluster() {
        return cluster;
    }
}