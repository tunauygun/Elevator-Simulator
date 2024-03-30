package Elevator;

import Common.*;

import static Common.SystemRequestType.*;

/**
 * IdleState.java
 * <p>
 * Represents the idle state of an elevator.
 *
 * @version 1.0, March 17, 2024
 */
public class IdleState implements ElevatorState {

    private Elevator elevator;

    /**
     * Constructs a new IdleState for the specified elevator.
     *
     * @param elevator The elevator associated with this state.
     */
    public IdleState(Elevator elevator) {
        this.elevator = elevator;
    }

    /**
     * Handles the behavior of the elevator in the IDLE state:
     * - Waits for the first/new elevator request from the scheduler.
     * - Determines the direction the elevator needs to move.
     * - Transitions to the CloseDoorState to close the door before starting to move.
     */
    @Override
    public void handleState() {
        elevator.setDirection(Direction.STOPPED);

        int elevatorId = elevator.getElevatorId();
        UDPSenderReceiver senderReceiver = elevator.getSenderReceiver();

        LogPrinter.print(elevatorId, "ELEVATOR " + elevatorId + " STATE: IDLE " + LogPrinter.getTimestamp());
        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Waiting for a request at floor " + elevator.getFloorNumber() + "!");

        // Wait for the first/new elevator request and receive it from scheduler
        do {
            senderReceiver.sendSystemRequest(new SystemRequest(NEW_PRIMARY_REQUEST, elevatorId));
            elevator.setPrimaryRequest(ElevatorRequest.deserializeRequest(senderReceiver.receiveResponse()));
            // TODO: Set the deadline time

            if (elevator.getPrimaryRequest() == null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }
            }
        } while (elevator.getPrimaryRequest() == null);

        // Determine the direction that the elevator needs to move
        if (elevator.getPrimaryRequest().getCurrentTargetFloor() == elevator.getFloorNumber()) {
            elevator.setDirection(Direction.STOPPED);
        } else if (elevator.getPrimaryRequest().getCurrentTargetFloor() - elevator.getFloorNumber() > 0) {
            elevator.setDirection(Direction.UP);
        } else {
            elevator.setDirection(Direction.DOWN);
        }

        LogPrinter.print(elevatorId, "New Primary Request for Elevator " + elevatorId + ": " + elevator.getPrimaryRequest() + " Direction: " + elevator.getDirection());

        // Go the closed state to close the door before starting moving
        elevator.setCurrentState(new CloseDoorState(elevator));
    }
}
