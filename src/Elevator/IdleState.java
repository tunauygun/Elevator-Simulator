package Elevator;

import Common.*;
import static Common.SystemRequestType.*;

public class IdleState implements ElevatorState{

    private Elevator elevator;

    public IdleState(Elevator elevator) {
        this.elevator = elevator;
    }

    @Override
    public void handleState() {
        int elevatorId = elevator.getElevatorId();
        UDPSenderReceiver senderReceiver = elevator.getSenderReceiver();

        LogPrinter.print(elevatorId, "ELEVATOR " + elevatorId + " STATE: IDLE");
        LogPrinter.print(elevatorId, "Elevator " + elevatorId +  " Waiting for a request at floor " + elevator.getFloorNumber() + "!");

        // Wait for the first/new elevator request and receive it from scheduler
        do {
            senderReceiver.sendSystemRequest(new SystemRequest(NEW_PRIMARY_REQUEST, elevatorId));
            elevator.setPrimaryRequest(ElevatorRequest.deserializeRequest(senderReceiver.receiveResponse()));
            if(elevator.getPrimaryRequest() == null){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
            }
        }while (elevator.getPrimaryRequest() == null);

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
