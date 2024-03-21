package Elevator;

import Common.LogPrinter;
import Common.SystemRequest;
import Common.UDPSenderReceiver;

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

        try {
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
            // TODO: If stop required, update deadline

            try {
                Thread.sleep(INCREMENTAL_MOVE_TIME);
            } catch (InterruptedException e) {
            }

            // Update the floor number based on direction
            elevator.setFloorNumberToNextFloor();
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Moved to next floor: " + elevator.getFloorNumber());

            // TODO: Add sleep times to the time variable (m value)

        } while (elevator.getPrimaryRequest().getCurrentTargetFloor() != nextFloorNumber && !isStopRequiredAtNextFloor);

        try {
            Thread.sleep(BASE_MOVE_TIME / 2);
        } catch (InterruptedException e) {
        }


        // TODO: Add sleep times to the time variable (b value)

        // TODO: Check if there is a  hard fault
        // TODO: Let scheduler know this elevator is not available
        // TODO: If there is a fault, shut down


        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Stopped at floor " + elevator.getFloorNumber());
        elevator.setMotorRunning(false);

        // TODO: Update the timestamp
        elevator.setCurrentState(new OpenDoorState(elevator));
    }
}

