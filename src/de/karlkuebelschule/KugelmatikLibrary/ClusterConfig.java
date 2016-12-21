package de.karlkuebelschule.KugelmatikLibrary;

import java.security.InvalidParameterException;

/**
 * Stellt die Konfiguration eines Clusters dar.
 */
public class ClusterConfig {
    private StepMode stepMode;
    private int tickTime;
    private boolean useBreak;

    /**
     * Erstellt eine neue ClusterConfig-Instanz mit Standardwerten
     */
    public ClusterConfig() {
        stepMode = StepMode.FullStep;
        tickTime = 1900;
        useBreak = true;
    }

    /**
     * Erstellt eine neue ClusterConfig-Instanz
     *
     * @param stepMode  Der Schrittmodus der Motoren des Clusters
     * @param delayTime Die Delaytime des Clusters
     * @param useBreak  Ob das Cluster die Bremse benutzen soll
     */
    public ClusterConfig(StepMode stepMode, int delayTime, boolean useBreak) {
        if (delayTime < 50 || delayTime > 15000)
            throw new InvalidParameterException("delayTime is out of range: " + delayTime);

        this.stepMode = stepMode;
        this.tickTime = delayTime;
        this.useBreak = useBreak;
    }

    public StepMode getStepMode() {
        return stepMode;
    }

    public void setStepMode(StepMode stepMode) {
        this.stepMode = stepMode;
    }

    public int getTickTime() {
        return tickTime;
    }

    public void setTickTime(int tickTime) {
        this.tickTime = tickTime;
    }

    public boolean getUseBreak() {
        return useBreak;
    }

    public void setUseBreak(boolean useBreak) {
        this.useBreak = useBreak;
    }
}
