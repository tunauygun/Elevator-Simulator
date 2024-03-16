package Floor;

import Common.Constants;
import Common.Direction;
import Common.ElevatorRequest;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Floor.Floor.java
 * <p>
 * The Floor.Floor models a single floor in a building. The floor handles the floor laps and the direction lamp
 * commands from the scheduler. The floor also checks for elevator events in the current floor and notifies the
 * scheduler through Floor.Floor Subsystem.
 *
 * @version 1.0, February 24, 2024
 */
public class Floor {
    private int floorNumber;

    // List of elevator request going in the same direction
    private ArrayList<ElevatorRequest> downRequests;
    private ArrayList<ElevatorRequest> upRequests;

    // Variables for the state of floor and direction lamps.
    // True when the lamp is on, false when the lamp is off
    private boolean floorLampUp;
    private boolean floorLampDown;
    private boolean[][] directionLamps;

    // Flags indicating if the floor is first/last
    private boolean isFirstFloor;
    private boolean isLastFloor;


    /**
     * Creates a new floor at the given floor number
     * @param floorNumber Floor.Floor number of the new floor
     * @param numberOfFloors Total number of floors in the building
     */
    public Floor(int floorNumber, int numberOfFloors) {
        this.floorNumber = floorNumber;
        this.downRequests = new ArrayList<>();
        this.upRequests = new ArrayList<>();
        this.directionLamps = new boolean[Constants.NUMBER_OF_ELEVATORS][2];
        this.floorLampUp = false;
        this.floorLampDown = false;
        this.isFirstFloor = floorNumber == 1;
        this.isLastFloor = floorNumber == numberOfFloors;
    }

    /**
     * Adds the request to the floor's request queue
     * @param event The new elevator request
     */
    public synchronized void addRequest(ElevatorRequest event) {
        switch (event.getDirection()) {
            case UP -> upRequests.add(event);
            case DOWN -> downRequests.add(event);
        }
    }

    /**
     * Checks if there are any elevator requests at the floor
     * @return True if there is at least one elevator requests at the floor, False otherwise.
     */
    public ElevatorRequest checkForRequests() {

        // Check the up requests and update the floor lamp if needed
        for (int i = 0; i < upRequests.size(); i++) {
            if (upRequests.get(i).getTime().isBefore(LocalTime.now())) {
                this.floorLampUp = true;
                return upRequests.remove(i);
            }
        }

        // Check the down requests and update the floor lamp if needed
        for (int i = 0; i < downRequests.size(); i++) {
            if (downRequests.get(i).getTime().isBefore(LocalTime.now())) {
                this.floorLampDown = true;
                return downRequests.remove(i);
            }
        }
        return null;
    }

    /**
     * Sets the state of the direction lamp for the given direction
     * @param direction The direction for lamp
     * @param state The state of the lamp. (On/Off)
     */
    public void setDirectionLamp(int elevatorId, Direction direction, boolean state) {
        if (direction == Direction.UP && !isLastFloor) {
            directionLamps[elevatorId][0] = state;
        } else if (direction == Direction.DOWN && !isFirstFloor) {
            directionLamps[elevatorId][0] = state;
        }
    }

    /**
     * Sets the state of the floor lamp for the given direction
     * @param direction The direction for lamp
     * @param state The state of the lamp. (On/Off)
     */
    public void setFloorLamp(Direction direction, boolean state) {
        if (direction == Direction.UP && !isLastFloor) {
            floorLampUp = state;
        } else if (direction == Direction.DOWN && !isFirstFloor) {
            floorLampDown = state;
        }
    }
}
