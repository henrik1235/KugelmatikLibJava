package de.karlkuebelschule.KugelmatikLibrary;

import java.util.ArrayList;

/**
 * Eine Hilfsklasse für Schrittmotoren
 */
public class StepperUtil {
    public static Stepper[] getAllChangedSteppers(Stepper[] steppers) {
        ArrayList<Stepper> changedSteppers = new ArrayList<>();
        for (Stepper stepper : steppers)
            if (stepper.hasDataChanged())
                changedSteppers.add(stepper);

        return changedSteppers.toArray(new Stepper[changedSteppers.size()]);
    }

    /**
     * Überprüft ob alle Stepper die gleiche Höhe und WaitTime haben
     *
     * @param steppers Die zu überprüfenden Stepper
     * @return true wenn alle Stepper gleich sind, false wenn ungleich
     */
    public static boolean allSteppersSameValues(Stepper[] steppers) {
        if (steppers == null)
            throw new IllegalArgumentException("steppers is null");
        if (steppers.length <= 1)
            return true;

        final int height = steppers[0].getHeight();
        final byte waitTime = steppers[0].getWaitTime();

        for (int i = 1; i < steppers.length; i++) {
            Stepper stepper = steppers[i];
            if (stepper.getHeight() != height || stepper.getWaitTime() != waitTime)
                return false;
        }
        return true;
    }
}
