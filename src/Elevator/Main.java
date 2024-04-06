package Elevator;

import Common.Constants;

import java.util.ArrayList;

/**
 * Main class for initializing and starting elevator subsystems and controllers.
 *
 * @version 1.0, March 17, 2024
 */
public class Main {
    public static void main(String[] args) {
        for (int elevatorId = 0; elevatorId < Constants.NUMBER_OF_ELEVATORS; elevatorId++) {
            // Instantiate Elevator and Floor Subsystem
            ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorId);
            Elevator elev = new Elevator(elevatorSubsystem, elevatorId, true);
            ElevatorController elevatorCont = new ElevatorController(elev, elevatorSubsystem);

            Thread elevator = new Thread(elev, "Elevator" + elevatorId);
            Thread elevatorController = new Thread(elevatorCont, "ElevatorController" + elevatorId);

            // Start the elevator and floor thread
            elevator.start();
            elevatorController.start();
        }
    }
}
