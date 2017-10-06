package de.karlkuebelschule.KugelmatikLibrary;

import java.net.SocketException;

/**
 * Repräsentiert eine Kugelmatik
 */
public class Kugelmatik {
    public static final int VERSION = 24;

    private Cluster[] clusters;
    private Log log;

    public Kugelmatik(IAddressProvider addressProvider) throws SocketException {
        this(addressProvider, new Log(LogLevel.Debug));
    }

    public Kugelmatik(IAddressProvider addressProvider, Log log) throws SocketException {
        if (addressProvider == null)
            throw new IllegalArgumentException("addressProvider is null");
        if (log == null)
            throw new IllegalArgumentException("log is null");

        this.log = log;
        clusters = new Cluster[Config.KugelmatikHeight * Config.KugelmatikWidth];

        getLog().info("Kugelmatik Library in Java... Version %d", VERSION);
        getLog().verbose("Creating clusters with address provider %s", addressProvider.getClass().getSimpleName());

        for (int x = 0; x < Config.KugelmatikWidth; x++)
            for (int y = 0; y < Config.KugelmatikHeight; y++)
                clusters[y * Config.KugelmatikWidth + x] = new Cluster(this, addressProvider.getAddress(x, y), x, y);
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    /**
     * Gibt alle Ressourcen frei und schließt die Verbindung zu allen Clustern.
     */
    public void free() {
        log.info("Kugelmatik.free(): Closing connections...");
        if (clusters != null)
            for (Cluster cluster : clusters)
                cluster.free();
    }

    /**
     * Sendet ein Ping an alle Cluster
     */
    public void sendPing() {
        getLog().verbose("Kugelmatik.sendPing()");
        for (Cluster cluster : clusters)
            cluster.sendPing();
    }

    /**
     * Lässt die grüne LED aller Cluster blinken
     */
    public void blinkGreen() {
        getLog().verbose("Kugelmatik.blinkGreen()");
        for (Cluster cluster : clusters)
            cluster.blinkGreen();
    }

    /**
     * Lässt die rote LED aller Cluster blinken
     */
    public void blinkRed() {
        getLog().verbose("Kugelmatik.blinkRed()");
        for (Cluster cluster : clusters)
            cluster.blinkRed();
    }

    public void sendStop() {
        getLog().verbose("Kugelmatik.sendStop()");
        for (Cluster cluster : clusters)
            cluster.sendStop();
    }

    /**
     * Sendet alle nicht bestätigten Packets neu
     *
     * @return Gibt true zurück, wenn ein Packet gesendet wurde
     */
    public boolean resendPackets() {
        getLog().verbose("Kugelmatik.resendPackets()");

        boolean anyPacketsSent = false;
        for (Cluster cluster : clusters)
            anyPacketsSent |= cluster.resendPackets();

        getLog().verbose("anyPacketSent = %s", anyPacketsSent ? "true" : "false");
        return anyPacketsSent;
    }

    /**
     * Setzt die Höhe aller Stepper auf eine Höhe
     *
     * @param height Die Höhe auf die alle Stepper gesetzt werden sollen
     */
    public void setAllSteppers(int height) {
        setAllSteppers(height, Config.DefaultWaitTime);
    }

    public void setAllSteppers(int height, byte waitTime) {
        getLog().verbose("moveAllSteppers(height = %d, waitTime = %d)", height, waitTime);
        for (Cluster cluster : clusters)
            cluster.setAllSteppers(height);
    }

    /**
     * Sendet alle Höhenänderungen ohne Garantie, dass das Paket ankommen wird.
     *
     * @return Gibt true zurück, wenn ein Packet gesendet wurde
     */
    public boolean sendMovementData() {
        return sendMovementData(false);
    }

    /**
     * Sendet alle Höhenänderungen an die Cluster
     *
     * @param guaranteed Gibt an, ob eine Bestätigung gesendet werden soll
     * @return Gibt true zurück, wenn ein Packet gesendet wurde
     */
    public boolean sendMovementData(boolean guaranteed) {
        return sendMovementData(guaranteed, false);
    }

    /**
     * Sendet alle Höhenänderungen an die Cluster
     *
     * @param guaranteed      Gibt an, ob eine Bestätigung gesendet werden soll
     * @param sendAllSteppers Gibt an, ob alle Stepper gesendet werden sollen, auch wenn die Daten sich nicht geändert haben.
     * @return Gibt true zurück, wenn ein Packet gesendet wurde
     */
    public boolean sendMovementData(boolean guaranteed, boolean sendAllSteppers) {
        boolean anyPacketsSend = false;
        for (Cluster cluster : clusters)
            anyPacketsSend |= cluster.sendMovementData(guaranteed, sendAllSteppers);
        return anyPacketsSend;
    }

    /**
     * Gibt true zurück, wenn es nicht beantwortete Packets gibt
     */
    public boolean isAnyPacketPending() {
        for (Cluster cluster : clusters)
            if (cluster.isAnyPacketPending())
                return true;
        return false;
    }

    /**
     * Gibt ein Cluster nach seiner Position zurück
     *
     * @param x Die x-Position des Clusters
     * @param y Die y-Position des Clusters
     * @return Das Cluster an der Position
     */
    public Cluster getClusterByPosition(int x, int y) {
        if (x < 0 || x >= Config.KugelmatikWidth)
            throw new IllegalArgumentException("x is ouf of range");
        if (y < 0 || y >= Config.KugelmatikHeight)
            throw new IllegalArgumentException("y is out of range");
        return clusters[y * Config.KugelmatikWidth + x];
    }

    /**
     * Gibt einen Stepper nach seiner absoluten Position zurück
     *
     * @param x Die x-Position des Steppers
     * @param y Die y-Position des Steppers
     * @return Der Stepper an der Position x und y
     */
    public Stepper getStepperByPosition(int x, int y) {
        if (x < 0 || x >= getStepperWidth())
            throw new IllegalArgumentException("x is ouf of range");
        if (y < 0 || y >= getStepperHeight())
            throw new IllegalArgumentException("y is out of range");

        byte cx = (byte) (x / Cluster.Width);
        byte cy = (byte) (y / Cluster.Height);
        byte sx = (byte) (x % Cluster.Width);
        byte sy = (byte) (y % Cluster.Height);
        return getClusterByPosition(cx, cy).getStepperByPosition(sx, sy);
    }

    public void setStepper(int x, int y, int height) {
        getStepperByPosition(x, y).set(height);
    }

    public void setStepper(int x, int y, int height, byte waitTime) {
        getStepperByPosition(x, y).set(height, waitTime);
    }

    /**
     * Gibt die Breite der Kugelmatik in Steppern zurück
     */
    public int getStepperWidth() {
        return Config.KugelmatikWidth * Cluster.Width;
    }

    /**
     * Gibt die Höhe der Kugelmatik in Steppern zurück
     */
    public int getStepperHeight() {
        return Config.KugelmatikHeight * Cluster.Height;
    }

    /**
     * Gibt einen Array mit allen Clustern zurück
     */
    public Cluster[] getAllCluster() {
        return clusters.clone();
    }

    /**
     * Prüft ob eine Verbindung zu irgendeinem Cluster besteht.
     * @return True, wenn eine Verbindung einem Cluster besteht.
     */
    public boolean isAnyClusterOnline() {
        for (Cluster cluster : clusters)
            if (cluster.checkConnection())
                return true;
        return false;
    }

    /**
     * Gibt das Log-Objekt der Kugelmatik zurück
     */
    public Log getLog() {
        return log;
    }
}