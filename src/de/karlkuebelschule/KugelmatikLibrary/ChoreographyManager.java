package de.karlkuebelschule.KugelmatikLibrary;

/**
 * Der ChoreographyManager berechnet die Bewegungen für eine Chereographie und sendet die Daten an die Kugelmatik.
 */
public class ChoreographyManager implements Runnable {
    private Kugelmatik kugelmatik;
    private IChoreography choreography;
    private int targetFPS;
    private int fps;

    private Thread thread;

    private volatile boolean stopRequested;
    private volatile boolean choreographyRunning = false;

    /**
     * Erstellt eine neue ChoreographyManager-Instanz
     *
     * @param kugelmatik   Die Kugelmatik auf der die Choreography abgespielt werden soll
     * @param targetFPS    Die Zielframerate die erreicht werden soll
     * @param choreography Die Choreography die abgespielt werden soll
     */
    public ChoreographyManager(Kugelmatik kugelmatik, int targetFPS, IChoreography choreography) {
        if (kugelmatik == null)
            throw new IllegalArgumentException("kugelmatik is null");
        if (targetFPS <= 0)
            throw new IllegalArgumentException("targetFPS is out of range");
        if (choreography == null)
            throw new IllegalArgumentException("choreography is null");

        this.kugelmatik = kugelmatik;
        this.targetFPS = targetFPS;
        this.choreography = choreography;

        thread = new Thread(this, "ChoreographyThread");
    }

    /**
     * Startet die Choreography
     */
    public void start() {
        if (choreographyRunning)
            return;

        stopRequested = false;
        thread.start();
    }

    /**
     * Zeigt den ersten Frame der Choreography
     */
    public void showFirstFrame() {
        setSteppers(0);
        kugelmatik.sendMovementData(true, true);
    }

    /**
     * Hält die Choreography an
     */
    public void stop() {
        if (choreographyRunning)
            stopRequested = true;
    }

    /**
     * Gibt zurück, ob die Choreography läuft
     */
    public boolean isChoreographyRunning() {
        return choreographyRunning;
    }

    /**
     * Gibt die aktuelle Framerate zurück
     */
    public int getFPS() {
        return fps;
    }

    private void setSteppers(long time) {
        for (int x = 0; x < kugelmatik.getStepperWidth(); x++)
            for (int y = 0; y < kugelmatik.getStepperHeight(); y++)
                kugelmatik.getStepperByPosition(x, y).set(choreography.getHeight(x, y, time, this));
    }

    @Override
    public void run() {
        showFirstFrame();

        while (kugelmatik.isAnyPacketPending()) {
            sleep(500);
            kugelmatik.resendPackets();
        }

        sleep(5000);
        choreographyRunning = true;
        long startTime = System.currentTimeMillis();

        long ticksRunning = 0;

        while (!stopRequested) {
            long frameStartTime = System.currentTimeMillis();
            long timeRunning = frameStartTime - startTime;

            setSteppers(timeRunning);

            if (ticksRunning % 200 == 0)
                kugelmatik.sendMovementData(false, true);
            else
                kugelmatik.sendMovementData();

            if (ticksRunning % 10 == 0)
                kugelmatik.sendPing();

            long frameTime = System.currentTimeMillis() - frameStartTime;
            long sleepTime = (long) (1000f / targetFPS) - frameTime; // berechnen wie lange der Thread schlafen soll um die TargetFPS zu erreichen
            if (sleepTime > 0)
                sleep(sleepTime);

            fps = (int) Math.ceil(1000f / frameTime);
            ticksRunning++;

        }
        choreographyRunning = false;
        stopRequested = false;
    }

    private void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}