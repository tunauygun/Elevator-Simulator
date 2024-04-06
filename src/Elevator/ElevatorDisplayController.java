package Elevator;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ElevatorDisplayController {
    //Controller Component for Elevator Display
    private ArrayList<Elevator> elevators = new ArrayList<Elevator>();

    public ElevatorDisplayController() {
        //Instances the Display Controller
    }

    public void addElevator(Elevator newElevator) {
        //Add Elevator to List of Elevators
        this.elevators.add(newElevator);

        //Update the Display
    }
}
