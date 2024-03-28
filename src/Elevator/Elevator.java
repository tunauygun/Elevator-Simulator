package Elevator;

import Common.*;
import Floor.FaultType;

/**
 * Elevator.java
 * <p>
 * The Elevator models an elevator in a building.The elevator class reads the elevator
 * requests sent by the Floor.Floor from the scheduler and processes them to complete all requests.
 *
 * @version 3.0, March 17, 2024
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
    double time, deadline;
    private UDPSenderReceiver senderReceiver;

    /**
     * Constructs a new Elevator instance.
     *
     * @param subsystem  The elevator subsystem to which this elevator belongs.
     * @param elevatorId The unique identifier for this elevator.
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
        this.time = 0.0;
        this.deadline = 0.0;
    }

    /**
     * Returns the next floor number based on current floor and the direction of the elevator
     *
     * @return The next floor number
     */
    public int getNextFloorNumber() {
        if (this.direction == Direction.UP) {
            return this.floorNumber + 1;
        }
        return this.floorNumber - 1;
    }
    public double getTime() {
        return time;
    }
    public void setTime(double var) {
        this.time = var + getTime();
    }

    public double getDeadline() {
        return deadline;
    }
    public void setDeadline(double var) {
        this.deadline = var + getDeadline();
    }
    public void synchDeadline() {this.deadline = this.time;}
    /**
     * Sets the floor number to the next floor based on the current direction.
     */
    public void setFloorNumberToNextFloor() {
        this.floorNumber = getNextFloorNumber();
    }

    /**
     * Gets the current state of the elevator.
     *
     * @return The current state.
     */
    public ElevatorState getCurrentState() {
        return currentState;
    }

    /**
     * Sets the current state of the elevator and handles the state behavior.
     *
     * @param currentState The new state to set.
     */
    public void setCurrentState(ElevatorState currentState) {
        this.currentState = currentState;
        this.currentState.handleState();
    }

    /**
     * Sets the door state.
     *
     * @param doorOpen Whether the door is open.
     */
    public void setDoorOpen(boolean doorOpen) {
        this.doorOpen = doorOpen;
    }

    /**
     * Gets the unique identifier of this elevator.
     *
     * @return The elevator ID.
     */
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
     * Checks if the elevator motor is running.
     *
     * @return True if the elevator is running, false otherwise.
     */
    public boolean isMotorRunning() {
        return motorRunning;
    }

    /**
     * Sets the motor running state.
     *
     * @param motorRunning Whether the motor is running.
     */
    public void setMotorRunning(boolean motorRunning) {
        this.motorRunning = motorRunning;
    }

    /**
     * Checks if the elevator door is open.
     *
     * @return True if the door is open, false otherwise.
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Gets the elevator subsystem to which this elevator belongs.
     *
     * @return The elevator subsystem.
     */
    public ElevatorSubsystem getSubsystem() {
        return subsystem;
    }

    /**
     * Sets the primary request for this elevator.
     *
     * @param primaryRequest The primary request.
     */
    public void setPrimaryRequest(ElevatorRequest primaryRequest) {
        this.primaryRequest = primaryRequest;
    }

    /**
     * Sets the direction of the elevator.
     *
     * @param direction The new direction.
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Gets the primary request associated with this elevator.
     *
     * @return The primary request.
     */
    public ElevatorRequest getPrimaryRequest() {
        return primaryRequest;
    }

    /**
     * Gets the UDP sender/receiver for communication.
     *
     * @return The sender/receiver.
     */
    public UDPSenderReceiver getSenderReceiver() {
        return senderReceiver;
    }

    public boolean hasTransientFault(){
        return this.subsystem.hasFault(FaultType.DOOR_FAULT, floorNumber);
    }

    public boolean hasHardFault(){
        return this.subsystem.hasFault(FaultType.FLOOR_TIMER_FAULT, floorNumber);
    }

    /**
     * Runs the elevator thread, handling its state transitions.
     */
    @Override
    public void run() {
        this.currentState.handleState();
    }

}
