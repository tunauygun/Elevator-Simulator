package Elevator;

import Common.*;

import java.time.Duration;
import java.time.LocalTime;

import static Common.Constants.*;
import static Common.SystemRequestType.*;

/**
 * OpenDoorState.java
 * <p>
 * Represents the state of an elevator when the door is opening.
 *
 * @version 1.0, March 17, 2024
 */
public class OpenDoorState implements ElevatorState {

    private Elevator elevator;

    /**
     * Constructs a new OpenDoorState for the specified elevator.
     *
     * @param elevator The elevator associated with this state.
     */
    public OpenDoorState(Elevator elevator) {
        this.elevator = elevator;
    }

    /**
     * Handles the behavior of the elevator in the OPEN_DOOR state:
     * - Opens the elevator door.
     * - Processes elevator requests that are completed by visiting the current floor.
     * - Checks if the primary request is completed.
     * - Transitions to the CloseDoorState or IdleState accordingly.
     */
    @Override
    public void handleState() {
        int elevatorId = elevator.getElevatorId();
        UDPSenderReceiver senderReceiver = elevator.getSenderReceiver();

        LogPrinter.print(elevatorId, "ELEVATOR " + elevatorId + " STATE: OPEN_DOOR " + LogPrinter.getTimestamp());

        boolean hasDoorFault = elevator.hasTransientFault();

        LocalTime startTime = LocalTime.now();

        // Wait for opening the door and loading
        try {
            int doorOpeningDelay = hasDoorFault ? (int) (LOADING_TIME * 1.10) : (LOADING_TIME / 2);
            Thread.sleep(doorOpeningDelay);
        } catch (InterruptedException e) {
        }

        LocalTime endTime = LocalTime.now();

        if(Duration.between(startTime, endTime).toMillis() * 0.95 > LOADING_TIME / 2){
            LogPrinter.printWarning("Elevator " + elevatorId + " has door fault at floor " + elevator.getFloorNumber());
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + ": Waiting 5 seconds before attempting again.");

            try {
                Thread.sleep(5000);
                LogPrinter.print(elevatorId, "Elevator " + elevatorId + ": Attempting again.");
                int doorOpeningDelay = (LOADING_TIME / 2);
                Thread.sleep(doorOpeningDelay);
            } catch (InterruptedException e) {
            }
        }

        // TODO: Add sleep times to the time variable
        elevator.setDoorOpen(true);


        // Process elevator requests that are completed by visiting current floor
        senderReceiver.sendSystemRequest(new SystemRequest(PROCESS_COMPLETED_REQUESTS, elevator.getFloorNumber(), elevator.getDirection(), elevatorId));

        LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Opened door at floor " + elevator.getFloorNumber());

        try{
            Thread.sleep((elevator.getSubsystem().getUnboardingPassengerCount() * BOARDING_TIME_PER_PASSENGER));
            LogPrinter.print(elevatorId, "Elevator " + elevatorId + " Unboarding Passenger Count: " + elevator.getSubsystem().getBoardingPassengerCount() + " WaitTime: " + ((elevator.getSubsystem().getBoardingPassengerCount() * BOARDING_TIME_PER_PASSENGER)));
        }catch (InterruptedException e){
        }


        // Check if the primary request is completed
        if (elevator.getPrimaryRequest().getCurrentTargetFloor() == elevator.getFloorNumber() && elevator.getPrimaryRequest().getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
            LogPrinter.print(elevatorId, " Completed primary request: " + elevator.getPrimaryRequest());
            elevator.getSubsystem().setElevatorLamps(elevator.getPrimaryRequest().getCarButton(), false);
            elevator.getSubsystem().updateCountersForUnboardingPassengers();

            // Get new request from queue
            senderReceiver.sendSystemRequest(new SystemRequest(NEW_PRIMARY_REQUEST, elevatorId));
            elevator.setPrimaryRequest(ElevatorRequest.deserializeRequest(senderReceiver.receiveResponse()));

            if (elevator.getPrimaryRequest() == null) {
                LogPrinter.print(elevatorId, "Elevator " + elevatorId + ": No request in queue, going to IDLE");
                elevator.setCurrentState(new IdleState(elevator));
            } else {
                LogPrinter.print(elevatorId, "Elevator " + elevatorId + ": New primary request: " + elevator.getPrimaryRequest());

                // Update the travel direction
                if (elevator.getPrimaryRequest().getCurrentTargetFloor() - elevator.getFloorNumber() > 0) {
                    elevator.setDirection(Direction.UP);
                } else {
                    elevator.setDirection(Direction.DOWN);
                }

                // Update lamp status
                Direction direction = elevator.getDirection();
                int floorNumber = elevator.getFloorNumber();
                senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_LAMPS, floorNumber, direction, false, elevatorId));
                senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, floorNumber, direction, true, elevatorId));
                LogPrinter.print(elevatorId, "Set floor lamp: Direction=" + direction + " FloorNumber=" + floorNumber + "state=" + false);
                LogPrinter.print(elevatorId, "Set floor direction lamp: Direction = " + direction + " FloorNumber = " + floorNumber + " state = " + true);

                elevator.setCurrentState(new CloseDoorState(elevator));
            }
        } else {
            // We didn't complete the primary request. Update lamps and sent the state to close the door
            Direction direction = elevator.getDirection();
            int floorNumber = elevator.getFloorNumber();
            senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_LAMPS, floorNumber, direction, false, elevatorId));
            senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, floorNumber, direction, true, elevatorId));
            LogPrinter.print(elevatorId, "Set floor lamp: Direction=" + direction + " FloorNumber=" + floorNumber + "state=" + false);
            LogPrinter.print(elevatorId, "Set floor direction lamp: Direction = " + direction + " FloorNumber = " + floorNumber + " state = " + true);

            elevator.setCurrentState(new CloseDoorState(elevator));
        }
    }
}

