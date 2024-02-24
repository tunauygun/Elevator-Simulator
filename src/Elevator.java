import java.util.ArrayList;
import java.util.Random;

/**
 * Elevator.java
 * <p>
 * The Elevator models an elevator in a building. For iterable 1, the elevator class reads the elevator
 * events sent by the Floor from the scheduler and sends them back.
 *
 * @version 2.0, February 22, 2024
 */
public class Elevator implements Runnable {

    private final int BASE_MOVE_TIME = 5762;
    private final int INCREMENTAL_MOVE_TIME = 2240;
    private final int LOADING_TIME = 11210;
    private Scheduler scheduler;
    private ElevatorRequest primaryRequest;

    //Queue for elevator events
    public ArrayList<ElevatorRequest> downRequests = new ArrayList<>();

    public ArrayList<ElevatorRequest> upRequests = new ArrayList<>();

    public boolean hasWaitingRequests;

    public void addNewRequest(ElevatorRequest request) {
        switch (request.getDirection()) {
            case UP -> upRequests.add(request);
            case DOWN -> downRequests.add(request);
        }
        hasWaitingRequests = true;
    }

    private enum ElevatorState {
        IDLE, CLOSE_DOOR, MOVING, OPEN_DOOR
    }

    private ElevatorState currentState;
    private Direction direction;
    private int floorNumber;
    public ArrayList<Boolean> elevatorLamps = new ArrayList<>();
    private boolean motorRunning;
    private boolean doorOpen;


    /**
     * Constructs an instance of the Elevator class
     *
     * @param scheduler The scheduler that synchronizes the elevator with the floor
     */
    public Elevator(Scheduler scheduler, int numberOfFloors) {
        this.scheduler = scheduler;
        this.currentState = ElevatorState.IDLE;
        this.direction = Direction.STOPPED;
        this.floorNumber = 1;
        this.primaryRequest = null;
        this.hasWaitingRequests = false;
        this.motorRunning = false;
        this.doorOpen = true;

        for (int i = 0; i < numberOfFloors; i++) {
            elevatorLamps.add(false);
        }
    }

    @Override
    public void run() {
        // Reads the elevator events sent by the floor and sends them back
        while (true) {
            processState();
        }
    }

    public void processState() {
        switch (currentState) {
            case IDLE:
                System.out.println("\nELEVATOR STATE: IDLE");
                System.out.println("Waiting for a request at floor " + floorNumber + "!");

                this.primaryRequest = scheduler.receiveFirstPrimaryRequest();

                if (this.primaryRequest.getCurrentTargetFloor() == this.floorNumber) { // We are already at passenger's floor
                    this.direction = Direction.STOPPED;
                    //this.primaryRequest.status = RequestStatus.PASSENGER_PICKED_UP;
                } else if (this.primaryRequest.getCurrentTargetFloor() - this.floorNumber > 0) {
                    this.direction = Direction.UP;
                } else {
                    this.direction = Direction.DOWN;
                }

                System.out.println("New Primary Request: " + this.primaryRequest + " Direction: " + direction);

                this.currentState = ElevatorState.CLOSE_DOOR;
                break;

            case CLOSE_DOOR:
                System.out.println("ELEVATOR STATE: CLOSE_DOOR");

                if (this.primaryRequest.getCurrentTargetFloor() == this.floorNumber && this.primaryRequest.getStatus() == RequestStatus.PENDING) {
                    this.primaryRequest.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.direction = this.primaryRequest.getDirection();
                    this.elevatorLamps.set(this.primaryRequest.getCarButton(), true);
                    System.out.println("Picked up passenger for primary request");
                }
                scheduler.processRequestsAtCurrentFloor(floorNumber, direction);

                System.out.println("Closing Door");
                try {
                    Thread.sleep(LOADING_TIME / 2);
                } catch (InterruptedException e) {
                }
                System.out.println("Door Closed");

                this.doorOpen = false;
                scheduler.setFloorDirectionLamp(floorNumber, direction, false);
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

                int nextFloorNumber;
                boolean isStopRequiredAtNextFloor;
                do {
                    nextFloorNumber = getNextFloorNumber();
                    isStopRequiredAtNextFloor = scheduler.isStopRequiredForFloor(nextFloorNumber, direction);

                    try {
                        Thread.sleep(INCREMENTAL_MOVE_TIME);
                    } catch (InterruptedException e) {
                    }

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

                try {
                    Thread.sleep(LOADING_TIME / 2);
                } catch (InterruptedException e) {
                }

                this.doorOpen = true;

                System.out.println("Opened door at floor " + floorNumber);

                scheduler.processCompletedRequests(floorNumber, direction);

                if (this.primaryRequest.getCurrentTargetFloor() == floorNumber && this.primaryRequest.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed primary request: " + primaryRequest);
                    this.elevatorLamps.set(this.primaryRequest.getCarButton(), false);

                    // Get new request from queue
                    this.primaryRequest = scheduler.receiveNewPrimaryRequest();

                    if (this.primaryRequest == null) {
                        System.out.println("No request in queue, going to IDLE");
                        this.currentState = ElevatorState.IDLE;
                    } else {
                        System.out.println("New primary request: " + primaryRequest);
                        this.currentState = ElevatorState.CLOSE_DOOR;

                        if (this.primaryRequest.getCurrentTargetFloor() - this.floorNumber > 0) {
                            this.direction = Direction.UP;
                        } else {
                            this.direction = Direction.DOWN;
                        }
                        scheduler.setFloorLamp(floorNumber, direction, false);
                        scheduler.setFloorDirectionLamp(floorNumber, direction, true);
                    }
                } else {
                    scheduler.setFloorLamp(floorNumber, direction, false);
                    scheduler.setFloorDirectionLamp(floorNumber, direction, true);
                    this.currentState = ElevatorState.CLOSE_DOOR;
                }

                break;
        }
    }

    private int getNextFloorNumber() {
        if (this.direction == Direction.UP) {
            return this.floorNumber + 1;
        }
        return this.floorNumber - 1;
    }
}
