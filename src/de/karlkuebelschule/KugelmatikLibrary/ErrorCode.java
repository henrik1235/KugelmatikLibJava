package de.karlkuebelschule.KugelmatikLibrary;

/**
 * Gibt die Fehler an, die auf beim Betrieb eines Clusters auftreten können.
 */
public enum ErrorCode {
    None(0),
    PacketTooShort(1),
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
    McpFault2(14),
    McpFault3(15),
    McpFault4(16),
    McpFault5(17),
    McpFault6(18),
    McpFault7(19),
    McpFault8(20),

    InternalWrongParameter(252),
    InternalLoopValuesWrong(253),
    InternalDefaultConfigFault(254),
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
