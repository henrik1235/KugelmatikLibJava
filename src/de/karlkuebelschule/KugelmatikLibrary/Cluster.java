package de.karlkuebelschule.KugelmatikLibrary;

import de.karlkuebelschule.KugelmatikLibrary.Protocol.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Repräsentiert ein Cluster der Kugelmatik
 */
public class Cluster {
    public static final byte Width = 5;
    public static final byte Height = 6;

    private Kugelmatik kugelmatik;
    private int x;
    private int y;

    private DatagramSocket socket;
    private DatagramIncomeListener incomeListener;

    private IPingChangedEventHandler pingChangedEventHandler;

    private ClusterInfo clusterInfo;
    private Stepper[] steppers;

    private int currentRevision = 1;
    private long lastSuccessfulPingTime = -1;
    private int ping = -1;

    private Map<Integer, Packet> packetsToAcknowledge;
    private Map<Integer, Long> packetsSentTimes;


    /**
     * Gibt eine neue Instanz eines Clusters zurück
     *
     * @param kugelmatik Die kugelmatik zu der
     * @param address    Die InetAdresse des Clusters
     * @param x          Die x-Koordinate der Position des Clusters in der kugelmatik
     * @param y          Die y-Koordinate der Position der Clusters in der kugelmatik
     */
    public Cluster(Kugelmatik kugelmatik, InetAddress address, int x, int y) {
        if (kugelmatik == null)
            throw new IllegalArgumentException("kugelmatik is null");
        if (x < 0)
            throw new IllegalArgumentException("x is out of range");
        if (y < 0)
            throw new IllegalArgumentException("y is out of range");


        packetsToAcknowledge = new HashMap<>();
        packetsSentTimes = new HashMap<>();

        this.kugelmatik = kugelmatik;
        this.x = x;
        this.y = y;

        steppers = new Stepper[Width * Height];

        // die Reihenfolge der beiden for-Schleifen darf sich nicht ändern
        // da die Firmware genau diese Reihenfolge der Stepper erwartet
        for (byte sX = 0; sX < Width; sX++)
            for (byte sY = 0; sY < Height; sY++)
                steppers[sY * Width + sX] = new Stepper(this, sX, sY);

        if (address != null) {
            try {
                socket = new DatagramSocket();
                socket.connect(address, Config.ProtocolPort);
                incomeListener = new DatagramIncomeListener(this, socket, "listen_" + x + "_" + y);
                incomeListener.listen();
            } catch (IOException e) {
                this.kugelmatik.getLog().error("Error while creating socket for %s with ip %s", getUserfriendlyName(), address.getHostAddress());
                e.printStackTrace();
            }

            sendPing();
        }
    }

    /**
     * Wird aufgerufen, wenn eine Verbindung hergestellt wurde.
     */
    private void onConnected() {
        kugelmatik.getLog().debug("%s [address = %s] onConnected()", getUserfriendlyName(), socket.getInetAddress().toString());

        packetsToAcknowledge.clear();
        packetsSentTimes.clear();

        resetRevision();
        sendGetData();
        sendGetClusterConfig();
    }

    private long getTimeSinceLastPing() {
        if (lastSuccessfulPingTime < 0)
            return Long.MAX_VALUE;
        return System.currentTimeMillis() - lastSuccessfulPingTime;
    }

    /**
     * Überprüft die Verbindung.
     */
    public boolean checkConnection() {
        if (!isConnected())
            return false;

        if (getTimeSinceLastPing() > 5000)
            setPing(-1);

        return isConnected();
    }

    /**
     * Gibt einen Wert zurück, der angibt, ob sich ein Stepper geändert hat.
     */
    public boolean hasStepperChanged() {
        for (Stepper stepper : steppers)
            if (stepper.hasDataChanged())
                return true;
        return false;
    }

    /**
     * Sendet alle noch austehende Packets deren Sendezeit mehr als MinimumResendTimeout zurück liegt
     *
     * @return True wenn Packets gesendet wurden
     */
    public boolean resendPackets() {
        boolean anyPacketsSend = false;

        for (Map.Entry<Integer, Packet> entry : packetsToAcknowledge.entrySet())
            if (System.currentTimeMillis() - packetsSentTimes.get(entry.getKey()) >= Config.MinimumResendTimeout)
                anyPacketsSend |= sendPacketInternal(entry.getValue(), true, entry.getKey());
        return anyPacketsSend;
    }

    /**
     * Sende ein Packet an das Cluster (ohne Garantie).
     *
     * @param packet Das Packet das gesendet werden soll
     */
    public boolean sendPacket(Packet packet) {
        return sendPacket(packet, false);
    }

    /**
     * Sende ein Packet an das Cluster, wahlweise mit Garantie.
     *
     * @param packet     Das Packet das gesendet werden soll
     * @param guaranteed Bei true mit Garantie, bei false ohne Garantie
     * @return Gibt zurück ob ein Packet gesendet wurde
     */
    public boolean sendPacket(Packet packet, boolean guaranteed) {
        return sendPacketInternal(packet, guaranteed, currentRevision++);
    }

    /**
     * Interne Methode zum senden eines Packets
     *
     * @param packet     Das Packet das gesendet werden soll
     * @param guaranteed Bei true mit Garantie, bei false ohne Garantie
     * @param revision   Die Revision mit der das Packet gesendet werden soll
     * @return Gibt zurück ob ein Packet gesendet wurde
     */
    protected boolean sendPacketInternal(Packet packet, boolean guaranteed, int revision) {
        if (packet == null)
            throw new IllegalArgumentException("packet is null");

        // bei keiner Verbindung Paket ignorieren
        if (socket == null)
            return false;

        if (!isConnected()) {
            if (Config.IgnoreGuaranteedWhenOffline)
                guaranteed = false;

            // Ping erlauben, sonst kann die Software nicht feststellen ob das Cluster verfügbar ist
            if (!packet.getType().equals(PacketType.Ping) && Config.IgnorePacketWhenOffline)
                return false;
        }

        if (guaranteed) {
            packetsToAcknowledge.put(currentRevision, packet);
            packetsSentTimes.put(currentRevision, System.currentTimeMillis());
        }

        // wenn Cluster antwortet, dann muss nicht der Flag zum Senden von einem Ack-Paket gesetzt werden, da
        // schon die Antwort vom Cluster als Ack-Paket dient
        if (packet.getType().doesClusterAnswer())
            guaranteed = false;

        try {
            DatagramPacket datagramPacket = packet.getPacket(guaranteed, currentRevision);
            kugelmatik.getLog().verbose("%s: Sent %s with rev %d", getUserfriendlyName(), packet.getType().name(), revision);
            socket.send(datagramPacket);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Bewegt alle Kugeln auf eine Höhe.
     *
     * @param height Die Höhe zu der sich die Kugeln bewegen sollen
     */
    public void setAllSteppers(int height) {
        if (height < 0 || height > Config.MaxHeight)
            throw new IllegalArgumentException("height is out of range");

        for (Stepper stepper : steppers)
            stepper.set(height);
    }

    /**
     * Sendet die Höhenänderungen an das Cluster ohne Garantie
     *
     * @return Gibt zurück ob Packets gesendet wurden
     */
    public boolean sendMovementData() {
        return sendMovementData(false);
    }

    public boolean sendMovementData(boolean guaranteed) {
        return sendMovementData(guaranteed, false);
    }

    /**
     * Sendet die Höhenänderungen an das Cluster
     *
     * @param guaranteed Bei true mit Garantie, bei false ohne Garantie
     * @return Gibt zurück ob Packets gesendet wurden
     */
    public boolean sendMovementData(boolean guaranteed, boolean sendAllSteppers) {
        boolean sentSomething = sendMovementDataInternal(guaranteed, sendAllSteppers);
        if (sentSomething)
            for (Stepper stepper : steppers)
                stepper.internalOnDataSent();

        if (getTimeSinceLastPing() > 1000)
            sendPing();

        return sentSomething;
    }

    /**
     * Interne Methode zum Senden der Höhenänderungen an das Cluster
     *
     * @param guaranteed Bei true mit Garantie, bei false ohne Garantie
     * @return Gibt zurück ob Packets gesendet wurden
     */
    protected boolean sendMovementDataInternal(boolean guaranteed, boolean sendAllSteppers) {
        // kein Stepper hat sich geändert und wir sollen nicht alle Stepper senden
        if (!hasStepperChanged() && !sendAllSteppers)
            return false;

        // geänderte Stepper filtern
        Stepper[] changedSteppers;
        if (sendAllSteppers)
            changedSteppers = steppers;
        else
            changedSteppers = StepperUtil.getAllChangedSteppers(steppers);

        // keine Stepper haben sich geändert
        if (changedSteppers.length == 0)
            return false;

        // nur ein Stepper hat sich geändert
        if (changedSteppers.length == 1) {
            // MoveStepper bewegt nur einen Stepper (X, Y)
            Stepper stepper = changedSteppers[0];
            return sendPacket(new MoveStepper(stepper.getX(), stepper.getY(), stepper.getHeight(), stepper.getWaitTime()), guaranteed);
        }

        // wenn alle Stepper (auch die ungeänderten) auf den gleichen Werten sind
        if (StepperUtil.allSteppersSameValues(steppers))
            return sendPacket(new MoveAllSteppers(steppers[0].getHeight(), steppers[0].getWaitTime()), guaranteed);

        // wenn die Anzahl der Stepper zu groß ist, dann
        // ist es uneffizent die Position für jeden Stepper zu senden
        if (changedSteppers.length < 6) {
            // prüfen ob alle geänderte Stepper die gleicehn Werte
            boolean allChangedSameValues = StepperUtil.allSteppersSameValues(changedSteppers);

            if (allChangedSameValues) // wenn ja, dann müssen wir nur die Positionen der geänderten Stepper senden
                return sendPacket(new MoveSteppers(changedSteppers, changedSteppers[0].getHeight(), changedSteppers[0].getWaitTime()), guaranteed);
            else // wenn nicht, dann müssen wir alle Daten (Position, Höhe und WaitTime) senden
                return sendPacket(new MoveSteppersArray(changedSteppers), guaranteed);
        }

        // für alle Stepper (auch ungeänderte) alle Daten senden in einem kompakten Format
        return sendPacket(new MoveAllSteppersArray(this), guaranteed);
    }

    /**
     * Setzt die Revisionszählung des Clusters zurück.
     */
    public void resetRevision() {
        sendPacket(new ResetRevision());
        currentRevision = 1;
    }

    /**
     * Lässt die grüne LED blinken.
     */
    public void blinkGreen() {
        sendPacket(new BlinkGreen());
    }

    /**
     * Lässt die rote LED blinken.
     */
    public void blinkRed() {
        sendPacket(new BlinkRed());
    }

    /**
     * Sendet ein Ping-Packet an das Cluster. Die Rundlaufzeit kann mit getPing() abgerufen werden.
     */
    public void sendPing() {
        checkConnection();
        sendPacket(new Ping(System.currentTimeMillis()));
    }

    /***
     * Gibt einen Wert zurück welcher angibt, ob noch eine Verbindung mit dem Cluster besteht.
     * @return
     */
    private boolean isConnected() {
        return ping >= 0;
    }

    /**
     * Sendet eine Stop-Befehl an das Cluster
     */
    public void sendStop() {
        sendPacket(new Stop(), true);
    }

    /**
     * Setze alle Kugeln des Clusters auf Home
     */
    public void sendHome() {
        sendPacket(new Home(), true);

        for (Stepper stepper : steppers)
            stepper.reset();
    }

    /**
     * Setzte eine Kugel des Klusters auf Home.
     *
     * @param x Die x-Koordinate der Kugel
     * @param y Die y-Koordinate der Kugel
     */
    public void sendHome(byte x, byte y) {
        Stepper stepper = getStepperByPosition(x, y);
        stepper.reset();
        sendPacket(new HomeStepper(x, y), true);
    }

    /**
     * Wickelt eine Kugel ab und wieder auf.
     *
     * @param x Die x-Koordinate der Kugel
     * @param y Die y-Koordinate der Kugel
     */
    public boolean sendFix(byte x, byte y) {
        Stepper stepper = getStepperByPosition(x, y);
        stepper.reset();
        return sendPacket(new Fix(x, y), true);
    }

    /**
     * Ruft den Status der Stepper vom Cluster ab
     */
    public void sendGetData() {
        sendPacket(new GetData());
    }

    /**
     * Ruft die Konfiguration des Clusters ab
     */
    public void sendGetClusterConfig() {
        sendPacket(new PacketInfo(true));
    }

    /**
     * Wird vom DatagramIncomeListener aufgerufen wenn ein neues Packet angekommen ist
     *
     * @param packet Das angekommene Packet das verarbeitet werden soll
     */
    public void onReceive(DatagramPacket packet) {
        if (packet.getLength() == 0)
            return;

        byte[] data = packet.getData();
        if (packet.getLength() < Packet.HeadSize || data[0] != 'K' || data[1] != 'K' || data[2] != 'S')
            return;

        DataInputStream input = new DataInputStream(new ByteArrayInputStream(data));
        try {
            // ersten 4 Bytes überspringen ("KKS" und guaranteed Flag)
            input.skip(4);

            PacketType type = PacketType.values()[input.read() - 1];
            int revision = BinaryHelper.flipByteOrder(input.readInt());

            // Paket bestätigen
            acknowledge(revision);

            kugelmatik.getLog().verbose(getUserfriendlyName() + ": Packet " + type.name() + " | Length: " + packet.getLength() + " | Revision: " + revision);
            switch (type) {
                case Ping:
                    // wir senden ein Ping Paket an das Cluster mit der Systemzeit von uns
                    // das Cluster sendet das Paket mit dem gleichen Inhalt wieder zurück
                    if (packet.getLength() - Packet.HeadSize != Long.BYTES)
                        break;

                    // mithilfe dieser Zeit können wir die Laufzeit berechnen
                    boolean wasNotConnected = !checkConnection();
                    lastSuccessfulPingTime = System.currentTimeMillis();

                    long sendTime = input.readLong();
                    setPing((int) (System.currentTimeMillis() - sendTime));

                    if (!wasNotConnected)
                        onConnected();
                    break;
                case Ack:
                    // ignore
                    break;
                case Info:
                    byte buildVersion = input.readByte();

                    BusyCommand currentBusyCommand = BusyCommand.getCommand(input.readByte());
                    int highestRevision = BinaryHelper.flipByteOrder(input.readInt());
                    ErrorCode lastErrorCode = ErrorCode.getCode(input.readByte());
                    int freeRam = BinaryHelper.flipByteOrder(input.readShort());

                    ClusterConfig config;
                    try {
                        config = new ClusterConfig(input);
                    }
                    catch(IOException e) {
                        e.printStackTrace();

                        config = new ClusterConfig();
                    }

                    int mcpStatus = 0;
                    int loopTime = 0;
                    int networkTime = 0;
                    int maxNetworkTime = 0;
                    int stepperTime = 0;
                    int upTime = 0;

                    if (buildVersion >= 17)
                    {
                        mcpStatus = input.readUnsignedByte();
                        loopTime = BinaryHelper.flipByteOrder(input.readInt());
                        networkTime = BinaryHelper.flipByteOrder(input.readInt());
                        maxNetworkTime = BinaryHelper.flipByteOrder(input.readInt());
                        stepperTime = BinaryHelper.flipByteOrder(input.readInt());
                        upTime = BinaryHelper.flipByteOrder(input.readInt());
                    }

                    clusterInfo = new ClusterInfo(buildVersion, currentBusyCommand, highestRevision, config,
                            lastErrorCode, freeRam, mcpStatus,
                            loopTime, networkTime, maxNetworkTime, stepperTime,
                            upTime);

                    break;
                case GetData:
                    for (byte x = 0; x < Width; x++) // for-Schleife muss mit Firmware übereinstimmen
                        for (byte y = 0; y < Height; y++) {
                            Stepper stepper = getStepperByPosition(x, y);


                            short height = BinaryHelper.flipByteOrder(input.readShort());
                            if (height < 0 || height > Config.MaxHeight)
                                continue; // Höhe ignorieren

                            byte waitTime = input.readByte();
                            stepper.setHeight(height);
                            stepper.setWaitTime(waitTime);
                        }
                    break;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acknowledge(int rev) {
        packetsToAcknowledge.remove(rev);
        packetsSentTimes.remove(rev);
    }

    /**
     * Setzt den PingChangedEventHandler
     *
     * @param eventHandler Der PingChangedEventHandler
     */
    public void setPingChangedEventHandler(IPingChangedEventHandler eventHandler) {
        pingChangedEventHandler = eventHandler;
    }

    /**
     * Setzt den Ping-Wert und ruft bei Änderungen das entsprechende Event auf.
     *
     * @param ping Der neue Ping-Wert
     */
    private void setPing(int ping) {
        if (this.ping != ping) {
            this.ping = ping;

            if (pingChangedEventHandler != null)
                pingChangedEventHandler.onPingChanged(this);
        }
    }

    /**
     * Ruft die Rundlaufzeit für das Cluster ab.
     */
    public int getPing() {
        return ping;
    }

    /**
     * Gibt die ClusterInfo zurück
     */
    public ClusterInfo getClusterInfo() {
        return clusterInfo;
    }

    /**
     * Ruft ab ob es nicht beantwortete Pakete gibt
     */
    public boolean isAnyPacketPending() {
        return packetsToAcknowledge.size() != 0;
    }

    /**
     * Ruft die Anzahl der nicht beantworteten Pakete ab
     */
    public int getPendingPacketsCount() {
        return packetsToAcknowledge.size();
    }

    /**
     * Gibt den Stepper an der entsprechenden Postion zurück
     *
     * @param x Die x-Koordinate des Steppers
     * @param y Die y-Koordinate des Steppers
     * @return Der Stepper an der Postion
     */
    public Stepper getStepperByPosition(int x, int y) {
        if (x < 0 || x >= Width)
            throw new IllegalArgumentException("x is out of range");
        if (y < 0 || y >= Height)
            throw new IllegalArgumentException("y is out of range");

        return steppers[y * Width + x];
    }

    /**
     * Gibt einen Stepper anhand seines Indexes zurück
     *
     * @param index Der Index des Steppers
     * @return Der Stepper an der Stelle index
     */
    public Stepper getStepperByIndex(int index) {
        if (index < 0 || index >= steppers.length)
            throw new IllegalArgumentException("index is out of range");

        return steppers[index];
    }

    /**
     * Gibt die X-Koordinate des Clusters zurück
     */
    public int getX() {
        return x;
    }

    /**
     * Gibt die Y-Koordinate des Clusters zurück
     */
    public int getY() {
        return y;
    }

    /**
     * Gibt einen benutzerfreundlichen Namen zurück.
     */
    public String getUserfriendlyName() {
        return String.format("Cluster [%d, %d]", x + 1, y + 1);
    }
}