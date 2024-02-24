import java.time.LocalTime;

/**
 * ElevatorRequest.java
 * <p>
 * The ElevatorRequest class acts as the data structure that encapsulates the
 * time, floor, and button data shared between Floor and Elevator threads.
 *
 * @version 1.0, February 2, 2024
 */
public class ElevatorRequest {


    private LocalTime time;
    private String floorButton;
    private int floor, carButton;
    private RequestStatus status;

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public LocalTime getTime() {
        return time;
    }

    public int getFloor() {
        return floor;
    }

    public int getCarButton() {
        return carButton;
    }

    public int getCurrentTargetFloor() {
        if (this.status == RequestStatus.PENDING) {
            return this.floor;
        }
        return this.carButton;
    }

    public Direction getDirection() {
        if (carButton == floor) {
            return null;
        } else if (carButton - floor > 0) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    /**
     * Constructs an instance of the elevator event
     *
     * @param time        The time stamp of the elevator event
     * @param floor       An integer representing the floor on which the passenger is making a request
     * @param floorButton A string consisting of either “up” or “down” indicating the direction of travel
     * @param carButton   An integer representing floor button within the elevator which is providing service
     *                    to the passenger
     */
    public ElevatorRequest(LocalTime time, int floor, String floorButton, int carButton) {
        this.time = time;
        this.floor = floor;
        this.floorButton = floorButton;
        this.carButton = carButton;
        this.status = RequestStatus.PENDING;
    }

    /**
     * Returns the string representation of the elevator event
     *
     * @return The string representation of the elevator event
     */
    @Override
    public String toString() {
        return String.format("|Floor: %s, Direction: %s, CarButton: %s|", floor, floorButton, carButton);
    }
}
