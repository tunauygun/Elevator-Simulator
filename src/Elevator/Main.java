package Elevator;

import Common.Constants;

public class Main {
    public static void main(String[] args) {

//        //Instantiate Elevator.Elevator and Floor.Floor Subsystem
//        int elevatorId = 0;
//        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorId);
//        Elevator elev = new Elevator(elevatorSubsystem, elevatorId);
//        ElevatorController elevatorCont = new ElevatorController(elev, elevatorSubsystem);
//
//        Thread elevator = new Thread(elev, "Elevator");
//        Thread elevatorController = new Thread(elevatorCont, "ElevatorController");
//
//        // Start the elevator and floor thread
//        elevator.start();
//        elevatorController.start();


        for (int elevatorId = 0; elevatorId < Constants.NUMBER_OF_ELEVATORS; elevatorId++) {
            // Instantiate Elevator.Elevator and Floor.Floor Subsystem
            ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(elevatorId);
            Elevator elev = new Elevator(elevatorSubsystem, elevatorId);
            ElevatorController elevatorCont = new ElevatorController(elev, elevatorSubsystem);

            Thread elevator = new Thread(elev, "Elevator" + elevatorId);
            Thread elevatorController = new Thread(elevatorCont, "ElevatorController" + elevatorId);

            // Start the elevator and floor thread
            elevator.start();
            elevatorController.start();
        }

    }
}
