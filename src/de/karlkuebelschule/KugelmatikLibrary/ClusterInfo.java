package de.karlkuebelschule.KugelmatikLibrary;

/**
 * Enthält alle Informationen über das Cluster.
 */
public class ClusterInfo {
    private byte buildVersion;
    private BusyCommand currentBusyCommand;
    private int highestRevision;
    private ClusterConfig config;
    private ErrorCode lastErrorCode;
    private int freeRam;
    private int mcpStatus;
    private int loopTime;
    private int networkTime;
    private int maxNetworkTime;
    private int stepperTime;
    private int uptime;

    public ClusterInfo(byte buildVersion, BusyCommand currentBusyCommand, int highestRevision,
                       ClusterConfig config, ErrorCode lastErrorCode, int freeRam, int mcpStatus,
                       int loopTime, int networkTime, int maxNetworkTime, int stepperTime, int uptime) {
        this.buildVersion = buildVersion;
        this.currentBusyCommand = currentBusyCommand;
        this.highestRevision = highestRevision;
        this.config = config;
        this.lastErrorCode = lastErrorCode;
        this.freeRam = freeRam;
        this.mcpStatus = mcpStatus;
        this.loopTime = loopTime;
        this.networkTime = networkTime;
        this.maxNetworkTime = maxNetworkTime;
        this.stepperTime = stepperTime;
        this.uptime = uptime;
    }

    /**
     * Gibt die BuildVersion der Firmware zurück.
     */
    public byte getBuildVersion() {
        return buildVersion;
    }

    /**
     * Gibt das BusyCommand zurueck, welcher am Cluster ausgefuehrt wird.
     */
    public BusyCommand getCurrentBusyCommand() {
        return currentBusyCommand;
    }

    /**
     * Gibt die höchste empfangene Revision des Clusters zurück.
     */
    public int getHighestRevision() {
        return highestRevision;
    }

    /**
     * Gibt die Konfiguration des Clusters zurück.
     */
    public ClusterConfig getConfig() {
        return config;
    }

    /**
     * Gibt den ErrorCode zurueck, welcher am Cluster zuletzt aufgetreten ist.
     */
    public ErrorCode getLastErrorCode() {
        return lastErrorCode;
    }

    /**
     * Gibt einen Wert in Bytes zurück, wie viel freier Arbeitsspeicher auf dem Cluster verfügbar ist.
     */
    public int getFreeRam() {
        return freeRam;
    }

    public int getMcpStatus() {
        return mcpStatus;
    }

    public int getLoopTime() {
        return loopTime;
    }

    public int getNetworkTime() {
        return networkTime;
    }

    public int getMaxNetworkTime() {
        return maxNetworkTime;
    }

    public int getStepperTime() {
        return stepperTime;
    }

    public int getUptime() {
        return uptime;
    }
}
