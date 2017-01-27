package de.karlkuebelschule.Kugelmatik.examples;

import de.karlkuebelschule.Kugelmatik.SimpleKugelmatik;
import de.karlkuebelschule.KugelmatikLibrary.Config;

public class HelloWorld extends SimpleKugelmatik {
    @Override
    protected void setup() {
        // Kugelmatik mit einem Cluster laden
        useOneCluster("192.168.178.40");

        // ganze Kugelmatik Decke benutzen
        // useKugelmatik();
    }

    @Override
    protected void loop() {
        // Sinus Welle berechnen
        double sineWave = Math.sin(seconds() / 10.0); // Wert zwischen -1 und 1
        sineWave += 1; // von [-1, 1] auf [0, 2] verschieben
        sineWave /= 2; // von [0, 2] auf [0, 1] normalisieren

        // Höhe in Schrittanzahl umwandeln
        int height = (int)Math.round(sineWave * Config.MaxHeight);

        // Kugel an 0, 4 setzen
        setStepper(0, 4, height);
    }
}
