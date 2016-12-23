package de.karlkuebelschule.KugelmatikLibrary;

import java.util.Arrays;

/**
 * Eine Hilfsklasse für Schrittmotoren
 */
public class StepperUtil {
    /**
     * Überprüft ob alle Stepper die gleiceh Höhe und WaitTime haben
     *
     * @param steppers Die zu überprüfenden Stepper
     * @return True wenn gleich, False wenn ungleich
     */
    public static boolean allSteppersSameValues(Stepper[] steppers) {
        if (steppers == null)
            throw new IllegalArgumentException("steppers is null");
        if (steppers.length <= 1)
            return true;

        final int height = steppers[0].getHeight();
        final byte waitTime = steppers[0].getWaitTime();

        return Arrays.stream(steppers)
                .filter(stepper -> stepper.getHeight() == height && stepper.getWaitTime() == waitTime).count() == steppers.length;
    }
}
