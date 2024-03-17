package Common;

import java.io.*;
import java.util.ArrayList;

public class ElevatorStatus implements Serializable {

    private final int elevatorId;
    private final Direction direction;
    private final int floorNumber;
    private final ArrayList<Integer> stopRequestFloorsGoingUp;
    private final ArrayList<Integer> stopRequestFloorsGoingDown;

    public ElevatorStatus(int elevatorId, Direction direction, int floorNumber, ArrayList<Integer> stopRequestFloorsGoingUp, ArrayList<Integer> stopRequestFloorsGoingDown) {
        this.elevatorId = elevatorId;
        this.direction = direction;
        this.floorNumber = floorNumber;
        this.stopRequestFloorsGoingUp = stopRequestFloorsGoingUp;
        this.stopRequestFloorsGoingDown = stopRequestFloorsGoingDown;
    }

    /**
     * Gets the elevators id.
     *
     * @return The elevator id.
     */
    public int getElevatorId() {
        return elevatorId;
    }

    /**
     * Gets the elevators direction of travel.
     *
     * @return The direction.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the elevators current floor.
     *
     * @return The floor number of elevator.
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Gets the list of floors where a stop is requested for given direction
     *
     * @param direction The direction of travel
     * @return The list of floor numbers
     */
    public ArrayList<Integer> getStopRequestFloorsInDirection(Direction direction) {
        return direction == Direction.UP ? stopRequestFloorsGoingUp : stopRequestFloorsGoingDown;
    }

    /**
     * Serializes an {@link ElevatorStatus} object into a byte array.
     *
     * @param status The {@link ElevatorStatus} to be serialized.
     * @return A byte array containing the serialized data.
     */
    public static byte[] serializeStatus(ElevatorStatus status) {
        byte[] statusBytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(status);
            statusBytes = bos.toByteArray();
        } catch (IOException e) {
            System.err.println(e);
            System.exit(1);
        }
        return statusBytes;
    }

    /**
     * Deserializes a byte array into an {@link ElevatorStatus} object.
     *
     * @param statusBytes The byte array containing the serialized data.
     * @return The deserialized {@link ElevatorStatus} object.
     */
    public static ElevatorStatus deserializeStatus(byte[] statusBytes) {
        ElevatorStatus deserializedStatus = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(statusBytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedStatus = (ElevatorStatus) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        }
        return deserializedStatus;
    }
}
