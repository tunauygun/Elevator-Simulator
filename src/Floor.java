import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Floor.java
 * <p>
 * The Floor models a single floor in a building. The floor handles the floor laps and the direction lamp
 * commands from the scheduler. The floor also checks for elevator events in the current floor and notifies the
 * scheduler through Floor Subsystem.
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
    private boolean directionLampUp;
    private boolean directionLampDown;
    private boolean floorLampUp;
    private boolean floorLampDown;

    // Flags indicating if the floor is first/last
    private boolean isFirstFloor;
    private boolean isLastFloor;


    /**
     * Creates a new floor at the given floor number
     * @param floorNumber Floor number of the new floor
     * @param numberOfFloors Total number of floors in the building
     */
    public Floor(int floorNumber, int numberOfFloors) {
        this.floorNumber = floorNumber;
        this.downRequests = new ArrayList<>();
        this.upRequests = new ArrayList<>();
        this.directionLampUp = false;
        this.directionLampDown = false;
        this.floorLampUp = false;
        this.floorLampDown = false;
        this.isFirstFloor = floorNumber == 1;
        this.isLastFloor = floorNumber == numberOfFloors;
    }

    /**
     * Adds the request to the floor's request queue
     * @param event The new elevator request
     */
    public void addRequest(ElevatorRequest event) {
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
    public void setDirectionLamp(Direction direction, boolean state) {
        if (direction == Direction.UP && !isLastFloor) {
            directionLampUp = state;
        } else if (direction == Direction.DOWN && !isFirstFloor) {
            directionLampDown = state;
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
