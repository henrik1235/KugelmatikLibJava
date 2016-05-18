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

    McpFault1(13),
    McpFault2(13),
    McpFault3(14),
    McpFault4(15),
    McpFault5(16),
    McpFault6(17),
    McpFault7(18),
    McpFault8(19),

    Internal(255),
    UnknownError(Integer.MAX_VALUE);

    private int value;

    ErrorCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ErrorCode getCode(byte value) {
        for (ErrorCode code : ErrorCode.values())
            if (code.getValue() == value)
                return code;
        return ErrorCode.UnknownError;
    }
}
