package Elevator;

import Common.LogPrinter;
import Common.RequestStatus;
import Common.SystemRequest;
import Common.UDPSenderReceiver;

import static Common.Constants.*;
import static Common.SystemRequestType.*;

/**
 * CloseDoorState.java
 * <p>
 * Represents the state of an elevator when the door is closing.
 *
 * @version 1.0, March 17, 2024
 */
public class CloseDoorState implements ElevatorState {

    private Elevator elevator;

    /**
     * Constructs a new CloseDoorState for the specified elevator.
     *
     * @param elevator The elevator associated with this state.
     */
    public CloseDoorState(Elevator elevator) {
        this.elevator = elevator;
    }

    /**
     * Handles the behavior of the elevator in the CLOSE_DOOR state:
     * - Checks if the passenger for the primary request has been picked up.
     * - Processes any request at the current floor by picking up passengers.
     * - Closes the elevator door.
     * - Sets floor direction lamps.
     * - Transitions to the MovingState.
     */
    @Override
    public void handleState() {
        int elevatorId = elevator.getElevatorId();
        UDPSenderReceiver senderReceiver = elevator.getSenderReceiver();

        LogPrinter.print(elevatorId, "ELEVATOR " + elevatorId + " STATE: CLOSE_DOOR");

        // Check if we picked up the passenger for the primary request
        if (elevator.getPrimaryRequest().getCurrentTargetFloor() == elevator.getFloorNumber() && elevator.getPrimaryRequest().getStatus() == RequestStatus.PENDING) {
            elevator.getPrimaryRequest().setStatus(RequestStatus.PASSENGER_PICKED_UP);
            elevator.setDirection(elevator.getPrimaryRequest().getDirection());
            elevator.getSubsystem().setElevatorLamps(elevator.getPrimaryRequest().getCarButton(), true);
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Picked up passenger for primary request");
        }

        // Process any request at the current floor by picking up passengers
        senderReceiver.sendSystemRequest(new SystemRequest(PROCESSES_REQUESTS_AT_CURRENT_FLOOR, elevator.getFloorNumber(), elevator.getDirection(), elevatorId));

        // Close the door
        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Closing Door");
        try {
            Thread.sleep(LOADING_TIME / 2);
        } catch (InterruptedException e) {
        }
        // TODO: Add sleep times to the time variable
        elevator.setTime(LOADING_TIME/2);
        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Door Closed");
        System.out.println(elevator.getTime());
        // Update the door and lamp flags
        elevator.setDoorOpen(false);
        senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, elevator.getFloorNumber(), elevator.getDirection(), false, elevatorId));
        LogPrinter.print(elevatorId, "Set floor direction lamp: Direction = " + elevator.getDirection() + " FloorNumber = " + elevator.getFloorNumber() + " state = " + false);

        elevator.setCurrentState(new MovingState(elevator));
    }
}
