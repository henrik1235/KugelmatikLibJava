package org.KarlKuebelSchule.KugelmatikLib;

/**
 * Gibt die Fehler an, die auf beim Betrieb eines Clusters auftreten können.
 */
public enum ErrorCode {
    None(0),
    TooShort(1),
    InvalidX(2),
    InvalidY(3),
    InvalidMagic(4),
    BufferOverflow(5),
    UnknownPacket(6),
    NotRunningBusy(7),
    InvalidConfigValue(8),
    InvalidHeight(9),
    InvalidValue(10),
    NotAllowedToRead(11),
    PacketSizeBufferOverflow(12),
    Internal(255);

    private byte value;

    ErrorCode(int value) {
        this.value = (byte)value;
    }

    public byte getValue() {
        return value;
    }

    public static ErrorCode getCode(byte value) {
        for (ErrorCode code : ErrorCode.values())
            if (code.getValue() == value)
                return code;
        return null;
    }
}
