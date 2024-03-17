package Elevator;

import Common.LogPrinter;
import Common.SystemRequest;
import Common.UDPSenderReceiver;

import static Common.Constants.*;
import static Common.SystemRequestType.*;

public class MovingState implements ElevatorState{

    private Elevator elevator;

    public MovingState(Elevator elevator) {
        this.elevator = elevator;
    }

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

            try {
                Thread.sleep(INCREMENTAL_MOVE_TIME);
            } catch (InterruptedException e) {
            }

            // Update the floor number based on direction
            elevator.setFloorNumberToNextFloor();
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Moved to next floor: " + elevator.getFloorNumber());

        } while (elevator.getPrimaryRequest().getCurrentTargetFloor() != nextFloorNumber && !isStopRequiredAtNextFloor);

        try {
            Thread.sleep(BASE_MOVE_TIME / 2);
        } catch (InterruptedException e) {
        }

        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Stopped at floor " + elevator.getFloorNumber());
        elevator.setMotorRunning(false);

        elevator.setCurrentState(new OpenDoorState(elevator));
    }
}

