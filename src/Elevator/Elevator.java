package Elevator;

import Common.*;

import static Common.SystemRequestType.*;

/**
 * Elevator.Elevator.java
 * <p>
 * The Elevator.Elevator models an elevator in a building.The elevator class reads the elevator
 * requests sent by the Floor.Floor from the scheduler and processes them to complete all requests.
 *
 * @version 2.0, February 24, 2024
 */
public class Elevator implements Runnable {

    ElevatorSubsystem subsystem;



    private enum ElevatorState {
        IDLE, CLOSE_DOOR, MOVING, OPEN_DOOR;
    }
    // TODO: Move these to the constants file

    private final int BASE_MOVE_TIME = 5762;
    private final int INCREMENTAL_MOVE_TIME = 2240;
    private final int LOADING_TIME = 11210;
    private ElevatorRequest primaryRequest;

    private ElevatorState currentState;

    private Direction direction;
    private int floorNumber;
    private boolean motorRunning;
    private boolean doorOpen;
    private int elevatorId;

    private UDPSenderReceiver senderReceiver;
    /**
     * Constructs an instance of the Elevator.Elevator class
     */
    public Elevator(ElevatorSubsystem subsystem, int elevatorId) {
        this.subsystem = subsystem;
        this.elevatorId = elevatorId;

        this.currentState = ElevatorState.IDLE;
        this.direction = Direction.STOPPED;
        this.floorNumber = 1;
        this.primaryRequest = null;

        this.motorRunning = false;
        this.doorOpen = true;

        this.senderReceiver = new UDPSenderReceiver(0, Constants.SCHEDULER_PORT);
    }

    /**
     * Returns the next floor number based on the direction of the elevator
     *
     * @return The next floor number
     */
    private int getNextFloorNumber() {
        if (this.direction == Direction.UP) {
            return this.floorNumber + 1;
        }
        return this.floorNumber - 1;
    }

    /**
     * Processes the states of the elevator
     */
    public void processState() {
        switch (currentState) {
            case IDLE:
                System.out.println("\nELEVATOR STATE: IDLE");
                System.out.println("Waiting for a request at floor " + floorNumber + "!");

                // Wait for the first/new elevator request and receive it from scheduler
                do {
                    senderReceiver.sendSystemRequest(new SystemRequest(NEW_PRIMARY_REQUEST, this.elevatorId));
                    this.primaryRequest = ElevatorRequest.deserializeRequest(senderReceiver.receiveResponse());
                    if(this.primaryRequest == null){
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {}
                    }
                }while (this.primaryRequest == null);

                // Determine the direction that the elevator needs to move
                if (this.primaryRequest.getCurrentTargetFloor() == this.floorNumber) {
                    this.direction = Direction.STOPPED;
                } else if (this.primaryRequest.getCurrentTargetFloor() - this.floorNumber > 0) {
                    this.direction = Direction.UP;
                } else {
                    this.direction = Direction.DOWN;
                }

                System.out.println("New Primary Request: " + this.primaryRequest + " Common.Direction: " + direction);

                // Go the closed state to close the door before starting moving
                this.currentState = ElevatorState.CLOSE_DOOR;
                break;

            case CLOSE_DOOR:
                System.out.println("ELEVATOR STATE: CLOSE_DOOR");

                // Check if we picked up the passenger for the primary request
                if (this.primaryRequest.getCurrentTargetFloor() == this.floorNumber && this.primaryRequest.getStatus() == RequestStatus.PENDING) {
                    this.primaryRequest.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.direction = this.primaryRequest.getDirection();
                    subsystem.setElevatorLamps(this.primaryRequest.getCarButton(), true);
                    System.out.println("Picked up passenger for primary request");
                }

                // Process any request at the current floor by picking up passengers
                senderReceiver.sendSystemRequest(new SystemRequest(PROCESSES_REQUESTS_AT_CURRENT_FLOOR, floorNumber, direction, elevatorId));

                // Close the door
                System.out.println("Closing Door");
                try {
                    Thread.sleep(LOADING_TIME / 2);
                } catch (InterruptedException e) {
                }
                System.out.println("Door Closed");

                // Update the door and lamp flags
                this.doorOpen = false;
                senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, floorNumber, direction, false, elevatorId));
                currentState = ElevatorState.MOVING;
                break;

            case MOVING:
                System.out.println("ELEVATOR STATE: MOVING");
                System.out.println("Current floor: " + floorNumber);

                this.motorRunning = true;
                try {
                    Thread.sleep(BASE_MOVE_TIME / 2);
                } catch (InterruptedException e) {
                }

                // Keep traveling floors until we reach the primary target floor or a floor with an elevator request
                int nextFloorNumber;
                boolean isStopRequiredAtNextFloor;
                do {
                    // Check if a stop is required at the next stop
                    nextFloorNumber = getNextFloorNumber();
                    senderReceiver.sendSystemRequest(new SystemRequest(IS_STOP_REQUIRED, nextFloorNumber, direction, elevatorId));
                    isStopRequiredAtNextFloor = senderReceiver.receiveResponse()[0] == 1;

                    try {
                        Thread.sleep(INCREMENTAL_MOVE_TIME);
                    } catch (InterruptedException e) {
                    }

                    // Update the floor number based on direction
                    if (this.direction == Direction.UP) {
                        this.floorNumber += 1;
                    } else {
                        this.floorNumber -= 1;
                    }
                    System.out.println("Moved to next floor: " + floorNumber);

                } while (this.primaryRequest.getCurrentTargetFloor() != nextFloorNumber && !isStopRequiredAtNextFloor);

                try {
                    Thread.sleep(BASE_MOVE_TIME / 2);
                } catch (InterruptedException e) {
                }

                System.out.println("Stopped at floor " + floorNumber);
                this.motorRunning = false;
                this.currentState = ElevatorState.OPEN_DOOR;
                break;

            case OPEN_DOOR:
                System.out.println("ELEVATOR STATE: OPEN_DOOR");

                // Wait for opening the door and loading
                try {
                    Thread.sleep(LOADING_TIME / 2);
                } catch (InterruptedException e) {
                }
                this.doorOpen = true;

                System.out.println("Opened door at floor " + floorNumber);

                // Process elevator requests that are completed by visiting current floor
                senderReceiver.sendSystemRequest(new SystemRequest(PROCESS_COMPLETED_REQUESTS, floorNumber, direction, elevatorId));

                // Check if the primary request is completed
                if (this.primaryRequest.getCurrentTargetFloor() == floorNumber && this.primaryRequest.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed primary request: " + primaryRequest);
                    subsystem.setElevatorLamps(this.primaryRequest.getCarButton(), false);

                    // Get new request from queue
                    senderReceiver.sendSystemRequest(new SystemRequest(NEW_PRIMARY_REQUEST));
                    this.primaryRequest = ElevatorRequest.deserializeRequest(senderReceiver.receiveResponse());

                    if (this.primaryRequest == null) {
                        System.out.println("No request in queue, going to IDLE");
                        this.currentState = ElevatorState.IDLE;
                    } else {
                        System.out.println("New primary request: " + primaryRequest);
                        this.currentState = ElevatorState.CLOSE_DOOR;

                        // Update the travel direction
                        if (this.primaryRequest.getCurrentTargetFloor() - this.floorNumber > 0) {
                            this.direction = Direction.UP;
                        } else {
                            this.direction = Direction.DOWN;
                        }

                        // Update lamp status
                        senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_LAMPS, floorNumber, direction, false, elevatorId));
                        senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, floorNumber, direction, true, elevatorId));
                    }
                } else {
                    // We didn't complete the primary request. Update lamps and sent the state to close the door
                    senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_LAMPS, floorNumber, direction, false, elevatorId));
                    senderReceiver.sendSystemRequest(new SystemRequest(SET_FLOOR_DIRECTION_LAMPS, floorNumber, direction, true, elevatorId));
                    this.currentState = ElevatorState.CLOSE_DOOR;
                }

                break;
        }
    }

    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * Returns the current direction of travel for the elevator
     *
     * @return direction of travel for the elevator
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Returns the current location of the elevator
     *
     * @return The floor where the elevator is currently located
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Returns true if the elevator is running, false otherwise.
     *
     * @return True if the elevator is running, false otherwise.
     */
    public boolean isMotorRunning() {
        return motorRunning;
    }

    /**
     * Returns the state of the door.
     *
     * @return True if the door is open, false otherwise.
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Keeps running the elevator by processing the states
     */
    @Override
    public void run() {
        while (true) {
            processState();
        }
    }
}
