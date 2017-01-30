package de.karlkuebelschule.Kugelmatik.examples;

import de.karlkuebelschule.Kugelmatik.SimpleKugelmatik;

public class ManyCluster extends SimpleKugelmatik {
    @Override
    protected void setup() {
        // Kugelmatik mit 2x2 Cluster laden
        String[][] clusters = new String[][] {
                { "192.168.88.21", "192.168.88.22" },
                { "192.168.88.11", "192.168.88.12" }
        };
        useClusters(clusters);

        // maximale Höhe auf 6000 Schritte setzen
        setMaxHeight(6000);
    }

    @Override
    protected void loop() {
        // Sinus Welle berechnen
        double sineWave = Math.sin(seconds() / 10.0); // Wert zwischen -1 und 1
        sineWave += 1; // von [-1, 1] auf [0, 2] verschieben
        sineWave /= 2; // von [0, 2] auf [0, 1] normalisieren

        // Höhe in Schrittanzahl umwandeln
        int height = (int)Math.round(sineWave * getMaxHeight());

        // alle Kugeln auf die Höhe setzen
        setAllSteppers(height);
    }
}
