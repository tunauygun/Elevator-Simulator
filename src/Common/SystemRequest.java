package Common;

import java.io.*;
import java.util.ArrayList;

/**
 * SystemRequest.java
 * <p>
 * Represents a `SystemRequest` in our application.
 * <p>
 * This class encapsulates data for various system requests related to elevators.
 *
 * @version 1.0, March 17, 2024
 */
public class SystemRequest implements Serializable {
    private int id;
    private int floorNumber;
    private Direction direction;
    private boolean state;
    private SystemRequestType type;
    private ElevatorRequest elevatorRequest;
    private ArrayList<ElevatorRequest> elevatorRequests;

    /**
     * Creates a system request with the specified type.
     *
     * @param type The type of the system request.
     */
    public SystemRequest(SystemRequestType type) {
        this.type = type;
    }

    /**
     * Creates a system request with the specified type and ID.
     *
     * @param type The type of the system request.
     * @param id   The id of the elevator.
     */
    public SystemRequest(SystemRequestType type, int id) {
        this.type = type;
        this.id = id;
    }

    /**
     * Creates a system request with the specified type, elevator requests, and ID.
     *
     * @param type             The type of the system request.
     * @param elevatorRequests The associated elevator requests.
     * @param id               The id of the elevator.
     */
    public SystemRequest(SystemRequestType type, ArrayList<ElevatorRequest> elevatorRequests, int id) {
        this.type = type;
        this.elevatorRequests = elevatorRequests;
        this.id = id;
    }

    /**
     * Creates a system request with the specified type, elevator request, and ID.
     *
     * @param type            The type of the system request.
     * @param elevatorRequest The associated elevator request.
     * @param id              The id of the elevator.
     */
    public SystemRequest(SystemRequestType type, ElevatorRequest elevatorRequest, int id) {
        this.type = type;
        this.elevatorRequest = elevatorRequest;
        this.id = id;
    }

    /**
     * Creates a system request with the specified type, floor number, direction, and ID.
     *
     * @param type        The type of the system request.
     * @param floorNumber The floor number associated with the request.
     * @param direction   The direction of the request (e.g., UP, DOWN).
     * @param id          The id of the elevator.
     */
    public SystemRequest(SystemRequestType type, int floorNumber, Direction direction, int id) {
        this.type = type;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.id = id;
    }

    /**
     * Creates a system request with the specified type, floor number, direction, state, and ID.
     *
     * @param type        The type of the system request.
     * @param floorNumber The floor number associated with the request.
     * @param direction   The direction of the request (e.g., UP, DOWN).
     * @param state       The state value related to the system request.
     * @param id          The id of the elevator.
     */
    public SystemRequest(SystemRequestType type, int floorNumber, Direction direction, boolean state, int id) {
        this.type = type;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.state = state;
        this.id = id;
    }

    /**
     * Serializes a {@link SystemRequest} object into a byte array.
     *
     * @param request The {@link SystemRequest} to be serialized.
     * @return A byte array containing the serialized data.
     */
    public static byte[] serializeRequest(SystemRequest request) {
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

    /**
     * Deserializes a byte array into a {@link SystemRequest} object.
     *
     * @param requestBytes The byte array containing the serialized data.
     * @return The deserialized {@link SystemRequest} object.
     */
    public static SystemRequest deserializeRequest(byte[] requestBytes) {
        SystemRequest deserializedRequest = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(requestBytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            deserializedRequest = (SystemRequest) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
            System.exit(1);
        }
        return deserializedRequest;
    }

    /**
     * Gets the elevator id associated with the request.
     *
     * @return The elevator id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the floor number related to the request.
     *
     * @return The floor number.
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Gets the direction of the request (e.g., UP, DOWN).
     *
     * @return The direction.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Gets the state value related to the system request.
     *
     * @return The value of state for the request.
     */
    public boolean getState() {
        return state;
    }

    /**
     * Gets the type of the system request.
     *
     * @return The request type.
     */
    public SystemRequestType getType() {
        return type;
    }


    /**
     * Gets the elevator request associated with this system request.
     *
     * @return The elevator request.
     */
    public ElevatorRequest getElevatorRequest() {
        return elevatorRequest;
    }

    /**
     * Returns a string representation of the SystemRequest.
     *
     * @return String representation of the SystemRequest.
     */
    @Override
    public String toString() {
        // ----- Request Types and Attributes to Include -----
        // REGISTER_ELEVATOR_CONTROLLER            elevatorID
        // NEW_PRIMARY_REQUEST                     elevatorID
        // ADD_NEW_REQUEST                         elevatorRequest
        // IS_STOP_REQUIRED                        floorNumber Direction   elevatorID
        // PROCESSES_REQUESTS_AT_CURRENT_FLOOR     floorNumber Direction   elevatorID
        // PROCESS_COMPLETED_REQUESTS              floorNumber Direction   elevatorID
        // SET_FLOOR_DIRECTION_LAMPS               floorNumber Direction   state   elevatorID
        // SET_FLOOR_LAMPS                         floorNumber Direction   state   elevatorID

        String str = "| RequestType = " + type + " ";
        if (type == SystemRequestType.ADD_NEW_REQUEST) {
            str += " ElevatorRequest = " + elevatorRequest;
        } else {
            str += "id = " + id;
        }

        if (type == SystemRequestType.IS_STOP_REQUIRED || type == SystemRequestType.PROCESSES_REQUESTS_AT_CURRENT_FLOOR || type == SystemRequestType.PROCESS_COMPLETED_REQUESTS || type == SystemRequestType.SET_FLOOR_DIRECTION_LAMPS || type == SystemRequestType.SET_FLOOR_LAMPS) {
            str += "; floorNumber = " + floorNumber + "; ";
            str += "direction = " + direction;
        }

        if (type == SystemRequestType.SET_FLOOR_DIRECTION_LAMPS || type == SystemRequestType.SET_FLOOR_LAMPS) {
            str += "; state = " + state;
        }

        str += " |";
        return str;
    }

    /**
     * Gets the list of elevator requests associated with this system request.
     *
     * @return The list of elevator requests.
     */
    public ArrayList<ElevatorRequest> getElevatorRequests() {
        return elevatorRequests;
    }
}
