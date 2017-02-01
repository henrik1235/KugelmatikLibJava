package de.karlkuebelschule.Kugelmatik;

import de.karlkuebelschule.KugelmatikLibrary.*;

import java.net.InetAddress;

public abstract class SimpleKugelmatik {
    private Log log = new Log(LogLevel.Debug);
    private Kugelmatik kugelmatik;

    private long lastPing = 0;
    private long lastData = 0;

    /**
     * Wird zu Beginn aufgerufen, wenn start() aufgerufen wird.
     */
    protected abstract void setup();
    /**
     * Wird in einer Dauerschleife ausgeführt, nachdem setup() aufgerufen wurde und erfolgreich eine Verbindung hergestellt wurde.
     */
    protected abstract void loop();

    @Override
    protected void finalize() throws Throwable {
        if (kugelmatik != null)
            kugelmatik.free();
        super.finalize();
    }

    /**
     * Ruft setup() auf und ruft dann wenn eine Verbindung zu einem Cluster besteht loop() in einer Dauerschleife auf.
     */
    public void start() {
        log.info("Running SimpleKugelmatik...");
        log.info("Class name: %s", getClass().getSimpleName());
        log.info("====================");

        log.info("Running setup()...");
        try {
            setup();

            if (kugelmatik == null)
                log.error("No Kugelmatik loaded. Use useOnCluster(), useClusters() or useKugelmatik() to load any clusters.");
            else if (!kugelmatik.isAnyClusterOnline())
                log.error("Not connected to any cluster!");
            else {
                log.info("====================");
                log.info("Running loop()...");

                try {
                    final long frameTime = 16;
                    while (true) {
                        long time = System.currentTimeMillis();

                        loop();
                        tick();

                        if (System.currentTimeMillis() - time < frameTime)
                            sleep(frameTime);
                    }
                } catch (Exception e) {
                    log.error("Exception while running loop():");
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e) {
            log.error("Exception while running setup():");
            e.printStackTrace();
        }

        if (kugelmatik != null)
            kugelmatik.free();
    }

    /**
     * Führt Aufgaben im Hintergrund aus um die Kugelmatik zu verwalten (Ping senden und Daten senden).
     */
    private void tick() {
        if (kugelmatik == null)
            return;

        final long pingInterval = 1000;
        final long dataInterval = 50;

        long time = System.currentTimeMillis();

        if (time - lastPing > pingInterval) {
            kugelmatik.sendPing();
            kugelmatik.resendPackets();

            lastPing = time;
        }

        if (time - lastData > dataInterval) {
            kugelmatik.sendMovementData();

            lastData = time;
        }
    }

    /**
     * Lädt die Kugelmatik mit nur einem Cluster mit der IP-Adresse ipAddress.
     * @param ipAddress Die IP-Adresse des Clusters zu welchem verbunden werden soll.
     */
    protected void useOneCluster(String ipAddress) {
        if (ipAddress == null)
            throw new IllegalArgumentException("ipAddress is null");

        try {
            log.info("useOneCluster(): Loading Kugelmatik with only one cluster: %s", ipAddress);
            loadKugelmatik(1, 1, new StaticAddressProvider(InetAddress.getByName(ipAddress)));
        }
        catch(Exception e) {
            log.error("Could not use one cluster! Exception:");
            e.printStackTrace();
        }
    }

    /**
     * Lädt die Kugelmatik mit einem 2D-Array an IP-Adressen für die Cluster.
     * @param ipAddresses Die IP-Adressen des Clusters zu welchen verbunden werden sollen.
     */
    protected void useClusters(String[][] ipAddresses) {
        if (ipAddresses == null)
            throw new IllegalArgumentException("ipAddresses is null");
        if (ipAddresses.length == 0)
            throw new IllegalArgumentException("ipAddresses is empty");
        if (ipAddresses[0] == null)
            throw new IllegalArgumentException("First array of ipAddresses is null");

        int width = ipAddresses.length;
        int height = ipAddresses[0].length;
        for (int i = 0; i < width; i++)
            if (ipAddresses[i] == null || ipAddresses[i].length != height)
                throw new IllegalArgumentException("ipAddresses is not rectangular");

        try {
            log.info("Loading Kugelmatik with %dx%d clusters", width, height);
            loadKugelmatik(width, height, new IAddressProvider() {
                @Override
                public InetAddress getAddress(int x, int y) {
                    try {
                        return InetAddress.getByName(ipAddresses[x][y]);
                    }
                    catch(Exception e) {
                        log.error("Could not use clusters! Exception:");
                        e.printStackTrace();
                        return null;
                    }
                }
            });
        }
        catch(Exception e) {
            log.error("Could not use clusters! Exception:");
            e.printStackTrace();
        }
    }

    /**
     * Lädt alle Cluster der ganzen Kugelmatik-Decke (5x5 Cluster).
     */
    protected void useKugelmatik() {
        useKugelmatik(5, 5);
    }

    /**
     * Lädt alle Cluster der ganzen Kugelmatik-Decke mit einer bestimmten Länge und Breite.
     * @param width Wie viele Cluster in der Breite geladen werden sollen.
     * @param height Wie viele Cluster in der Höhe geladen werden sollen.
     */
    protected void useKugelmatik(int width, int height) {
        log.info("Loading Kugelmatik with %dx%d clusters", width, height);
        loadKugelmatik(width, height, new StandardAddressProvider());
    }

    private void loadKugelmatik(int width, int height, IAddressProvider provider) {
        if (width <= 0)
            throw new IllegalArgumentException("width is out of range");
        if (height <= 0)
            throw new IllegalArgumentException("height is out of range");
        if (provider == null)
            throw new IllegalArgumentException("provider is null");

        if (kugelmatik != null)
            throw new IllegalStateException("Kugelmatik already loaded");

        try {
            // Config setzen
            Config.KugelmatikWidth = width;
            Config.KugelmatikHeight = height;

            // Kugelmatik laden
            kugelmatik = new Kugelmatik(provider, log);

            // auf Verbindung warten
            log.info("Waiting for connection to any cluster...");
            while (!isAnyClusterOnline())
                sleep(500);

            log.info("Connected!");
            log.info("Ready.");
        }
        catch(Exception e) {
            log.error("Could not load Kugelmatik! Exception:");
            e.printStackTrace();
        }
    }

    /**
     * Gibt die maximale Höhe zurück.
     * @return Die maximale Höhe (Schrittanzahl) für die Kugeln.
     */
    public int getMaxHeight() {
        return Config.MaxHeight;
    }

    /**
     * Setzt die maximale Höhe für die Kugeln.
     * @param maxHeight Die maximale Höhe (Schrittanzahl) der Kugeln.
     */
    public void setMaxHeight(int maxHeight) {
        if (maxHeight < 0 || maxHeight > Short.MAX_VALUE)
            throw new IllegalArgumentException("maxHeight is out of range");
        Config.MaxHeight = (short)maxHeight;
    }

    /**
     * Gibt die Anzahl der Kugeln in der Breite (X Richtung) zurück.
     * @return Die Anzahl der Kugeln in der Breite (X Richtung).
     */
    public int getWidth() {
        if (kugelmatik == null)
            return 0;
        return kugelmatik.getStepperWidth();
    }

    /**
     * Gibt die Anzahl der Kugeln in der Länge (Y Richtung) zurück.
     * @return Die Anzahl der Kugeln in der Länge (Y Richtung).
     */
    public int getHeight() {
        if (kugelmatik == null)
            return 0;
        return kugelmatik.getStepperHeight();
    }

    /**
     * Gibt die Höhe der Kugel an der Position (x, y) zurück.
     * @param x Die absolute Position in der Breite.
     * @param y Die absolute Position in der Höhe.
     * @return Die Höhe (Schrittanzahl) der Kugel.
     */
    public int getStepper(int x, int y) {
        if (kugelmatik == null) {
            log.error("getStepper(): No Kugelmatik loaded!");
            return 0;
        }

        return kugelmatik.getStepperByPosition(x, y).getHeight();
    }

    /**
     * Setzt die Kugel an der Position (x, y) auf die Höhe height.
     * @param x Die absolute Position in der Breite.
     * @param y Die absolute Position in der Höhe.
     * @param height Die Höhe (Schrittanzahl) an die die Kugel fahren soll.
     */
    public void setStepper(int x, int y, int height) {
        if (kugelmatik == null) {
            log.error("setStepper(): No Kugelmatik loaded!");
            return;
        }

        kugelmatik.setStepper(x, y, height);
        tick();
    }

    /**
     * Setzt alle Kugeln auf die gleiche Höhe height.
     * @param height Die Höhe (Schrittanzahl) an die die Kugeln fahren sollen.
     */
    public void setAllSteppers(int height) {
        if (kugelmatik == null) {
            log.error("setAllStepper(): No Kugelmatik loaded!");
            return;
        }

        kugelmatik.setAllSteppers(height);
        tick();
    }

    /**
     * Gibt die aktuelle Laufzeit des Systems in Sekunden zurück.
     * @return Die aktuelle Laufzeit des Systems in Sekunden zurück.
     */
    public int seconds() {
        return millis() / 1000;
    }

    /**
     * Gibt die aktuelle Laufzeit des Systems in Millisekunden zurück.
     * @return Die aktuelle Laufzeit des Systems in Millisekunden zurück.
     */
    public int millis() {
        return (int)System.currentTimeMillis();
    }

    /**
     * Wartet timeMilliseconds Millisekunden.
     * @param timeMilliseconds Die Zeit in Millisekunden die gewartet werden soll.
     */
    public void delay(long timeMilliseconds) {
        sleep(timeMilliseconds);
    }

    /**
     * Wartet timeMilliseconds Millisekunden.
     * @param timeMilliseconds Die Zeit in Millisekunden die gewartet werden soll.
     */
    public void sleep(long timeMilliseconds) {
        if (timeMilliseconds <= 0)
            throw new IllegalArgumentException("timeMilliseconds must be then larger than 0");

        try {
            Thread.sleep(timeMilliseconds);
            tick();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prüft ob eine Verbindung zu irgendeinem Cluster besteht.
     * @return True, wenn eine Verbindung zu einem Cluster besteht.
     */
    public boolean isAnyClusterOnline() {
        if (kugelmatik == null)
            return false;
        return kugelmatik.isAnyClusterOnline();
    }

    public Kugelmatik getKugelmatik() {
        return kugelmatik;
    }

    public Log getLog() {
        return log;
    }
}
