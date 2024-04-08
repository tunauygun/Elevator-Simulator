package Elevator;

import Common.Constants;
import Common.LogPrinter;
import Display.DisplayView;

import java.util.ArrayList;

/**
 * Main class for initializing and starting elevator subsystems and controllers.
 *
 * @version 1.0, March 17, 2024
 */
public class Main {
    public static void main(String[] args) {
        //ArrayList for Storing Elevator Objects


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

        //Create the Elevator UI
        DisplayView display = new DisplayView(Elevator.elevList);
        Thread displayView = new Thread(display, "DisplayView");
        displayView.start();
    }
}
