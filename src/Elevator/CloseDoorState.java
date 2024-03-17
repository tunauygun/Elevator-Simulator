package Elevator;

import Common.LogPrinter;
import Common.RequestStatus;
import Common.SystemRequest;
import Common.UDPSenderReceiver;

import static Common.Constants.*;
import static Common.SystemRequestType.*;

public class CloseDoorState implements ElevatorState{

    private Elevator elevator;

    public CloseDoorState(Elevator elevator) {
        this.elevator = elevator;
    }

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
        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Door Closed");

        // Update the door and lamp flags
        elevator.setDoorOpen(false);
        senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, elevator.getFloorNumber(), elevator.getDirection(), false, elevatorId));
        LogPrinter.print(elevatorId, "Set floor direction lamp: Direction = " + elevator.getDirection() + " FloorNumber = " + elevator.getFloorNumber() + " state = " + false);

        elevator.setCurrentState(new MovingState(elevator));
    }
}
