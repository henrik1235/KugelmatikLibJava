package de.karlkuebelschule.KugelmatikLibrary;

/**
 * Gibt alle Busy-Befehle an.
 * Bei einem Busy-Befehl geht das Cluster in einen blockierenden (Busy) Zustand über.
 */
public enum BusyCommand {
    None((byte) 0),
    Home((byte) 1),
    Fix((byte) 2),
    HomeStepper((byte) 3),
    Unknown(Byte.MAX_VALUE);

    private byte value;

    BusyCommand(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static BusyCommand getCommand(byte value) {
        for (BusyCommand code : BusyCommand.values())
            if (code.getValue() == value)
                return code;
        return BusyCommand.Unknown;
    }
}
