package Elevator;

import Common.*;

import java.util.ArrayList;

import static Common.Constants.*;
import static Common.SystemRequestType.*;

/**
 * MovingState.java
 * <p>
 * Represents the moving state of an elevator.
 *
 * @version 1.0, March 17, 2024
 */
public class MovingState implements ElevatorState {

    private Elevator elevator;

    /**
     * Constructs a new MovingState for the specified elevator.
     *
     * @param elevator The elevator associated with this state.
     */
    public MovingState(Elevator elevator) {
        this.elevator = elevator;
    }

    /**
     * Handles the behavior of the elevator in the MOVING state:
     * - Moves the elevator to the next floor until reaching the primary target floor or a floor with an elevator request.
     * - Transitions to the OpenDoorState to open the door.
     */
    @Override
    public void handleState() {
        int elevatorId = elevator.getElevatorId();
        UDPSenderReceiver senderReceiver = elevator.getSenderReceiver();

        LogPrinter.print(elevatorId, "ELEVATOR " + elevatorId + " STATE: MOVING");
        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Current floor: " + elevator.getFloorNumber());

        elevator.setMotorRunning(true);
;
        // Set the expected deadline for the elevator (1.5x the total move time per floor)
        elevator.synchDeadline();
        elevator.setDeadline(1.5 * BASE_MOVE_TIME);

        try {
            elevator.setTime(BASE_MOVE_TIME / 2);
            Thread.sleep(BASE_MOVE_TIME / 2);
        } catch (InterruptedException e) {
        }

        // Keep traveling floors until we reach the primary target floor or a floor with an elevator request
        int nextFloorNumber;
        boolean isStopRequiredAtNextFloor;
        do {
            // Check if a stop is required at the next stop
            nextFloorNumber = elevator.getNextFloorNumber();
            senderReceiver.sendSystemRequest(new SystemRequest(IS_STOP_REQUIRED, nextFloorNumber, elevator.getDirection(), elevatorId));
            isStopRequiredAtNextFloor = senderReceiver.receiveResponse()[0] == 1;

            //Increase deadline
            elevator.setDeadline(1.5 * INCREMENTAL_MOVE_TIME);

            //Simulate Elevator Moving, increase time
            try {
                elevator.setTime(INCREMENTAL_MOVE_TIME);
                Thread.sleep(INCREMENTAL_MOVE_TIME);
            } catch (InterruptedException e) {
            }

            // Update the floor number based on direction
            elevator.setFloorNumberToNextFloor();
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Moved to next floor: " + elevator.getFloorNumber());

        } while (elevator.getPrimaryRequest().getCurrentTargetFloor() != nextFloorNumber && !isStopRequiredAtNextFloor);

        try {
            elevator.setTime(BASE_MOVE_TIME / 2);
            Thread.sleep(BASE_MOVE_TIME / 2);
        } catch (InterruptedException e) {
        }

        //Check for Hard Fault
        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Deadline: " + elevator.getDeadline() + " Time: " + elevator.getTime());

        if (elevator.hasHardFault() || (elevator.getTime() > elevator.getDeadline())) {

            ArrayList<ElevatorRequest> requests =  elevator.getSubsystem().getWaitingRequests();
            if (elevator.getPrimaryRequest().getStatus() != RequestStatus.PASSENGER_PICKED_UP) {
                requests.add(elevator.getPrimaryRequest());
            }

            senderReceiver.sendSystemRequest(new SystemRequest(ELEVATOR_SHUTDOWN_REQUEST, requests, elevatorId));

            // TODO: Let scheduler know this elevator is not available
            // Print Error Message
            LogPrinter.printError("Error: Elevator " + elevatorId + " FloorTimerFault at floor " + elevator.getFloorNumber());
            // TODO: If there is a fault, shut down
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + " is shutting down");
            elevator.setMotorRunning(false);
            return;
        }

        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Stopped at floor " + elevator.getFloorNumber());
        elevator.setMotorRunning(false);

        elevator.setCurrentState(new OpenDoorState(elevator));
    }
}

