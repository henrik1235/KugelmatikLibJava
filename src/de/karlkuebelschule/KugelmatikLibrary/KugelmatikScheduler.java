package de.karlkuebelschule.KugelmatikLibrary;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Hilfsklasse um Aufgaben wie Ping regelmäßig aufzurufen
 */
public class KugelmatikScheduler {
    private Kugelmatik kugelmatik;
    private Timer timer;

    /**
     * Erstellt eine neue Scheduler-Instanz
     *
     * @param kugelmatik Die Kugelmatik für welche die Aktionen ausgeführt werden sollen
     */
    public KugelmatikScheduler(Kugelmatik kugelmatik) {
        if (kugelmatik == null)
            throw new IllegalArgumentException("kugelmatik is null");

        this.kugelmatik = kugelmatik;
        timer = new Timer();
    }

    /**
     * Ruft SendPing() in einem gewissen Interval auf
     *
     * @param interval Das Interval in dem SendPing aufgerufen werden soll
     */
    public void schedulePing(long interval) {
        scheduleTimerTask(new PingTask(), interval);
    }

    /**
     * Ruft ResenPackets() in einem gewissen Interval auf
     *
     * @param interval Das Interval in dem ResendPackets aufgerufen werden soll
     */
    public void scheduleResend(long interval) {
        scheduleTimerTask(new ResendTask(), interval);
    }

    /**
     * Ruft einen TimerTask in einem gewissen Interval auf
     *
     * @param task     Der aufzurufende TimerTask
     * @param interval Das Interval in dem der TimerTask aufgerufen werden soll
     */
    protected void scheduleTimerTask(TimerTask task, long interval) {
        if (interval <= 0)
            throw new IllegalArgumentException("interval must be greater than 0");

        timer.scheduleAtFixedRate(task, 0, interval);
    }

    /**
     * Löscht alle vorher definierten Tasks
     */
    public void removeAll() {
        timer.purge();
    }

    /**
     * Gibt das Kugelmatik object zurück, welches mit dem Schedule verknüpft ist
     */
    public Kugelmatik getKugelmatik() {
        return kugelmatik;
    }

    private class PingTask extends TimerTask {
        @Override
        public void run() {
            kugelmatik.sendPing();
        }
    }

    private class ResendTask extends TimerTask {
        @Override
        public void run() {
            kugelmatik.resendPackets();
        }
    }
}
