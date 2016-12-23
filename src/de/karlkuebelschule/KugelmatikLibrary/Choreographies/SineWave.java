package de.karlkuebelschule.KugelmatikLibrary.Choreographies;

import de.karlkuebelschule.KugelmatikLibrary.ChoreographyManager;
import de.karlkuebelschule.KugelmatikLibrary.Config;
import de.karlkuebelschule.KugelmatikLibrary.IChoreography;

/**
 * Eine Choreography um eine bewegte Sinuswelle darzustellen.
 */
public class SineWave implements IChoreography {
    private WaveDirection direction;
    private float timeFactor;
    private float frequency;

    /**
     * Erstellt eine neue SineWave-Instanz
     *
     * @param direction  Die Richtung der Sinuswelle
     * @param timeFactor Der Zeitfaktor der Sinuswelle
     * @param frequency  Die Frequenz der Sinuswelle
     */
    public SineWave(WaveDirection direction, float timeFactor, float frequency) {
        if (frequency == 0)
            throw new IllegalArgumentException("frequency is out of range");

        this.direction = direction;
        this.timeFactor = timeFactor;
        this.frequency = frequency;
    }

    @Override
    public int getHeight(int x, int y, long millis, ChoreographyManager choreographyManager) {
        float v = x;
        if (direction == WaveDirection.Y)
            v = y;

        // Sinuswelle berechnen
        double sinWave = Math.sin((v + millis * timeFactor) * frequency);

        sinWave += 1; // in den Bereich [0, 2] verschieben
        sinWave /= 2; // normalisieren

        return (int) Math.round(sinWave * Config.MaxHeight);
    }

}
