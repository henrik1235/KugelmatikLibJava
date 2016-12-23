package de.karlkuebelschule.KugelmatikLibrary;

public enum BrakeMode {
    None((byte) 0),
    Always((byte) 1),
    Smart((byte) 2);

    private byte numVal;

    BrakeMode(byte numVal) {
        this.numVal = numVal;
    }

    /**
     * Gibt den byte-Wert des Eintrags zurück.
     *
     * @return Der byte-Wert des Eintrags
     */
    public byte getByteValue() {
        return numVal;
    }
}