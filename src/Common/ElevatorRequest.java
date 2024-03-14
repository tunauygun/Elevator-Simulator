package Common;

import java.io.*;
import java.time.LocalTime;

/**
 * Common.ElevatorRequest.java
 * <p>
 * The Common.ElevatorRequest class acts as the data structure that encapsulates the
 * time, floor, and button data shared between Floor.Floor and Elevator.Elevator threads.
 *
 * @version 2.0, February 24, 2024
 */
public class ElevatorRequest implements Serializable {

    private LocalTime time;
    private String floorButton;
    private int floor, carButton;
    private RequestStatus status;

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
     * Returns the status of the elevator request
     * @return status of the elevator request
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Sets the elevator request status to the given status
     * @param status New status of the request
     */
    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    /**
     * The timestamp of the request
     * @return Request timestamp
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * Returns the floor on which the passenger is making a request
     * @return The floor on which the passenger is making a request
     */
    public int getFloor() {
        return floor;
    }

    /**
     * The floor number that the passenger needs to travel.
     * @return The requested target floor number
     */
    public int getCarButton() {
        return carButton;
    }

    /**
     * Indicates the floor number that the elevator needs to go to address the request at its current state.
     * If the passenger is not picked up, returns the "floor"; if the passanger is already picked up, returns
     * the "carButton"
     * @return The curent target floor for processing the request
     */
    public int getCurrentTargetFloor() {
        if (this.status == RequestStatus.PENDING) {
            return this.floor;
        }
        return this.carButton;
    }

    /**
     * Returns the direction based on the floor where the request is made and the destination floor
     * @return The direction of travel
     */
    public Direction getDirection() {
        if (carButton == floor) {
            return null;
        } else if (carButton - floor > 0) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    /**
     * Returns the string representation of the elevator event
     *
     * @return The string representation of the elevator event
     */
    @Override
    public String toString() {
        return String.format("|Floor.Floor: %s, Common.Direction: %s, CarButton: %s|", floor, floorButton, carButton);
    }

    public static byte[] serializeRequest(ElevatorRequest request) {
        byte[] requestBytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(request);
            requestBytes = bos.toByteArray();
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return requestBytes;
    }

    public static ElevatorRequest deserializeRequest(byte[] requestBytes) {
        ElevatorRequest deserializedRequest = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(requestBytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedRequest = (ElevatorRequest) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        }
        return deserializedRequest;
    }
}
