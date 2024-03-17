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

    private ElevatorSubsystem subsystem;
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
        this.direction = Direction.STOPPED;
        this.floorNumber = 1;
        this.primaryRequest = null;
        this.motorRunning = false;
        this.doorOpen = true;
        this.senderReceiver = new UDPSenderReceiver(0, Constants.SCHEDULER_PORT);
        this.currentState = new IdleState(this);
    }

    /**
     * Returns the next floor number based on the direction of the elevator
     *
     * @return The next floor number
     */
    public int getNextFloorNumber() {
        if (this.direction == Direction.UP) {
            return this.floorNumber + 1;
        }
        return this.floorNumber - 1;
    }

    public void setFloorNumberToNextFloor(){
        this.floorNumber = getNextFloorNumber();
    }

    public Elevator.ElevatorState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(ElevatorState currentState) {
        this.currentState = currentState;
        this.currentState.handleState();
    }

    public void setDoorOpen(boolean doorOpen) {
        this.doorOpen = doorOpen;
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

    public void setMotorRunning(boolean motorRunning) {
        this.motorRunning = motorRunning;
    }

    /**
     * Returns the state of the door.
     *
     * @return True if the door is open, false otherwise.
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    public ElevatorSubsystem getSubsystem() {
        return subsystem;
    }

    public void setPrimaryRequest(ElevatorRequest primaryRequest) {
        this.primaryRequest = primaryRequest;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public ElevatorRequest getPrimaryRequest() {
        return primaryRequest;
    }

    public UDPSenderReceiver getSenderReceiver() {
        return senderReceiver;
    }

    /**
     * Keeps running the elevator by processing the states
     */
    @Override
    public void run() {
        this.currentState.handleState();
    }
}
