package de.karlkuebelschule.KugelmatikTest;

import de.karlkuebelschule.KugelmatikLibrary.*;

import java.net.InetAddress;

public class Program {
    public static void main(String[] args) {
        Log log = new Log(LogLevel.Verbose);
        Kugelmatik kugelmatik = null;

        try {
            log.info("KugelmatikTest running...");
            log.info("=========================");

            InetAddress clusterAddress = InetAddress.getByName("192.168.178.40");
            log.info("Connecting to %s", clusterAddress.toString());

            // Config setzen
            Config.KugelmatikWidth = 1;
            Config.KugelmatikHeight = 1;

            kugelmatik = new Kugelmatik(new StaticAddressProvider(clusterAddress), log);
            Cluster cluster = kugelmatik.getClusterByPosition(0, 0);

            log.info("Waiting for connection...");

            cluster.resetRevision();

            while (!cluster.checkConnection()) {
                cluster.sendPing();
                sleep(500);
            }
            log.info("Got connection, ping: %dms", cluster.getPing());

            sleep(2500);

            log.info("Move stepper (0, 4) to 1000");
            kugelmatik.setStepper(0, 4, 1000);
            kugelmatik.sendMovementData();

            sleep(5000);

            log.info("Move all steppers to 0");
            kugelmatik.setAllSteppers(0);
            kugelmatik.sendMovementData(false, true);

            sleep(5000);

            log.info("Move stepper array (same height) to 500");
            kugelmatik.setStepper(0, 4, 500);
            kugelmatik.setStepper(0, 5, 500);
            kugelmatik.sendMovementData();

            sleep(5000);

            log.info("Move stepper array (different height) to 1000 and 250");
            kugelmatik.setStepper(0, 4, 1000);
            kugelmatik.setStepper(0, 5, 250);
            kugelmatik.sendMovementData();

            sleep(5000);

            log.info("Test all steppers");
            for (int x = 0; x < kugelmatik.getStepperWidth(); x++)
                for (int y = 0; y < kugelmatik.getStepperHeight(); y++)
                    kugelmatik.setStepper(x, y, x * 100 + x);
            kugelmatik.sendMovementData();

            sleep(5000);

            log.info("Move all steppers to 0");

            kugelmatik.setAllSteppers(0);
            kugelmatik.sendMovementData();

            log.info("========================================================");
            log.info("Done....");
        } catch (Exception e) {
            log.error("Whoooopps! Internal error while testing:");
            e.printStackTrace();
        }

        if (kugelmatik != null)
            kugelmatik.free();

        sleep(Long.MAX_VALUE);
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
