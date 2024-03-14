package Elevator;

import Floor.FloorSubsystem;
import Scheduler.Scheduler;

public class Main {
    public static void main(String[] args) {

        //Instantiate Elevator.Elevator and Floor.Floor Subsystem
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
        Elevator elev = new Elevator(elevatorSubsystem, 0);
        ElevatorController elevatorCont = new ElevatorController(elev, elevatorSubsystem);

        Thread elevator = new Thread(elev, "Elevator");
        Thread elevatorController = new Thread(elevatorCont, "ElevatorController");

        // Start the elevator and floor thread
        elevator.start();
        elevatorController.start();
    }
}
