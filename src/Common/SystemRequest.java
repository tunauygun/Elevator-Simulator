package Common;

import java.io.*;

public class SystemRequest implements Serializable {

    private int id;
    private int floorNumber;
    private Direction direction;
    private boolean state;
    private SystemRequestType type;
    private ElevatorRequest elevatorRequest;

    public int getId() {
        return id;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean getState() {
        return state;
    }

    public SystemRequestType getType() {
        return type;
    }

    public SystemRequest(SystemRequestType type) {
        this.type = type;
    }

    public ElevatorRequest getElevatorRequest() {
        return elevatorRequest;
    }

    public SystemRequest(SystemRequestType type, ElevatorRequest elevatorRequest, int id) {
        this.type = type;
        this.elevatorRequest = elevatorRequest;
        this.id = id;
    }

    public SystemRequest(SystemRequestType type, int id) {
        this.type = type;
        this.id = id;
    }

    public SystemRequest(SystemRequestType type, int floorNumber, Direction direction, int id) {
        this.type = type;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.id = id;
    }

    public SystemRequest(SystemRequestType type, int floorNumber, Direction direction, boolean state, int id) {
        this.type = type;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.state = state;
        this.id = id;
    }

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
        if(type == SystemRequestType.ADD_NEW_REQUEST){
            str += " ElevatorRequest = " + elevatorRequest;
        }else{
            str += "id = " + id;
        }

        if(type == SystemRequestType.IS_STOP_REQUIRED || type == SystemRequestType.PROCESSES_REQUESTS_AT_CURRENT_FLOOR || type == SystemRequestType.PROCESS_COMPLETED_REQUESTS || type == SystemRequestType.SET_FLOOR_DIRECTION_LAMPS || type == SystemRequestType.SET_FLOOR_LAMPS){
            str += "; floorNumber = " + floorNumber + "; ";
            str += "direction = " + direction;
        }

        if(type == SystemRequestType.SET_FLOOR_DIRECTION_LAMPS || type == SystemRequestType.SET_FLOOR_LAMPS ){
            str += "; state = " + state;
        }

        str += " |";
        return str;
    }
}
