package de.karlkuebelschule.KugelmatikLibrary;


import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Der Thread wartet auf ein ankommendes Packet an dem DatagramSocket
 */
public class DatagramIncomeListener implements Runnable {
    private Thread thread;
    private String name;

    private DatagramSocket socket;
    private Cluster cluster;

    /**
     * Erstellt eine neue DatagramIncomeListener-Instanz
     *
     * @param cluster Das Cluster das bei einem neuen Packet informiert werden soll
     * @param socket  Der Socket auf dem gehorcht werden soll
     * @param name    Der Name des Listeners (entscheidend für den Thread)
     */
    public DatagramIncomeListener(Cluster cluster, DatagramSocket socket, String name) {
        if (cluster == null)
            throw new IllegalArgumentException("cluster is null");
        if (socket == null)
            throw new IllegalArgumentException("socket is null");
        if (name == null)
            throw new IllegalArgumentException("name is null");

        this.cluster = cluster;
        this.socket = socket;
        this.name = name;
    }

    /**
     * Beginnt auf ankommende Packets zu warten
     */
    public void listen() {
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }

    /**
     * Beendet den Listener.
     */
    public void free() {
        if (thread != null)
            thread.interrupt();
    }

    @Override
    public void run() {
        byte[] buffer = new byte[Config.ReceiveBufferLength];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        while (!thread.isInterrupted()) {
            try {
                if (socket.isClosed())
                    return;

                socket.receive(packet);
                cluster.onReceive(packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
