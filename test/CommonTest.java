import Common.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CommonTest.java
 * <p>
 * JUnit tests for the classes in the Common package.
 *
 * @version 1.0, March 17, 2024
 */
public class CommonTest {
    /**
     * Tests the Constants class for correct constant values.
     */
    @Test
    public void testConstants() {
        assertEquals(10, Constants.NUMBER_OF_FLOORS);
        assertEquals(50000, Constants.SCHEDULER_PORT);
        assertEquals(5, Constants.NUMBER_OF_ELEVATORS);
        assertEquals(50001, Constants.SCHEDULER_PORT_2);
        assertEquals(50002, Constants.FLOOR_CONTROLLER_PORT);
    }

    /**
     * Tests the Direction enum for correct values.
     */
    @Test
    public void testDirection() {
        Direction direction1 = Direction.UP;
        Direction direction2 = Direction.DOWN;
        Direction direction3 = Direction.STOPPED;

        assertEquals(Direction.UP, direction1);
        assertEquals(Direction.DOWN, direction2);
        assertEquals(Direction.STOPPED, direction3);
    }

    /**
     * Tests the ElevatorRequest class for functionality and serialization/deserialization.
     */
    @Test
    public void testElevatorRequest() {
        // Create an ElevatorRequest object for testing
        LocalTime time = LocalTime.now();
        ElevatorRequest request = new ElevatorRequest(time, 5, "up", 7);

        // Test getStatus and setStatus methods
        assertEquals(RequestStatus.PENDING, request.getStatus());
        request.setStatus(RequestStatus.PASSENGER_PICKED_UP);
        assertEquals(RequestStatus.PASSENGER_PICKED_UP, request.getStatus());

        // Test getTime, getFloor, getCarButton, and getFloorButton methods
        assertEquals(time, request.getTime());
        assertEquals(5, request.getFloor());
        assertEquals(7, request.getCarButton());
        assertEquals("up", request.getFloorButton());

        // Test getCurrentTargetFloor and getDirection methods
        assertEquals(7, request.getCurrentTargetFloor());
        assertEquals(Direction.UP, request.getDirection());

        // Serialize and deserialize the ElevatorRequest object
        byte[] serializedRequest = ElevatorRequest.serializeRequest(request);
        ElevatorRequest deserializedRequest = ElevatorRequest.deserializeRequest(serializedRequest);

        // Verify that the deserialized object is equal to the original object
        assertEquals(request.getTime(), deserializedRequest.getTime());
        assertEquals(request.getFloor(), deserializedRequest.getFloor());
        assertEquals(request.getCarButton(), deserializedRequest.getCarButton());
        assertEquals(request.getFloorButton(), deserializedRequest.getFloorButton());
        assertEquals(request.getStatus(), deserializedRequest.getStatus());
        assertEquals(request.getCurrentTargetFloor(), deserializedRequest.getCurrentTargetFloor());
        assertEquals(request.getDirection(), deserializedRequest.getDirection());
    }

    /**
     * Tests the LogPrinter class for correct console output.
     */
    @Test
    public void testLogPrinterPrintMethod() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        LogPrinter.print(0, "Testing LogPrinter");

        assertEquals("\u001B[38;2;235;50;50mTesting LogPrinter\u001B[0m\n", outContent.toString());
    }

    /**
     * Tests the RequestStatus enum for correct string representations.
     */
    @Test
    public void testRequestStatus() {
        assertEquals("PENDING", RequestStatus.PENDING.toString());
        assertEquals("PASSENGER_PICKED_UP", RequestStatus.PASSENGER_PICKED_UP.toString());
    }

    /**
     * Tests the SystemRequest class for constructor, getter methods, toString, and serialization/deserialization.
     */
    @Test
    public void testSystemRequest() {
        // Create an ElevatorRequest object for testing
        ElevatorRequest elevatorRequest = new ElevatorRequest(null, 3, "up", 5);

        // Test constructor and getter methods
        SystemRequest systemRequest1 = new SystemRequest(SystemRequestType.ADD_NEW_REQUEST, elevatorRequest, 1);
        assertEquals(SystemRequestType.ADD_NEW_REQUEST, systemRequest1.getType());
        assertEquals(elevatorRequest, systemRequest1.getElevatorRequest());
        assertEquals(1, systemRequest1.getId());

        SystemRequest systemRequest2 = new SystemRequest(SystemRequestType.REGISTER_ELEVATOR_CONTROLLER, 2);
        assertEquals(SystemRequestType.REGISTER_ELEVATOR_CONTROLLER, systemRequest2.getType());
        assertEquals(2, systemRequest2.getId());

        SystemRequest systemRequest3 = new SystemRequest(SystemRequestType.IS_STOP_REQUIRED, 4);
        assertEquals(SystemRequestType.IS_STOP_REQUIRED, systemRequest3.getType());
        assertEquals(4, systemRequest3.getId());

        SystemRequest systemRequest4 = new SystemRequest(SystemRequestType.SET_FLOOR_LAMPS, 3, Direction.DOWN, true, 6);
        assertEquals(SystemRequestType.SET_FLOOR_LAMPS, systemRequest4.getType());
        assertEquals(3, systemRequest4.getFloorNumber());
        assertEquals(Direction.DOWN, systemRequest4.getDirection());
        assertTrue(systemRequest4.getState());
        assertEquals(6, systemRequest4.getId());

        // Test toString method
        assertEquals("| RequestType = ADD_NEW_REQUEST  ElevatorRequest = |Floor: 3, Direction: up, CarButton: 5| |", systemRequest1.toString());
        assertEquals("| RequestType = REGISTER_ELEVATOR_CONTROLLER id = 2 |", systemRequest2.toString());
        assertEquals("| RequestType = IS_STOP_REQUIRED id = 4; floorNumber = 0; direction = null |", systemRequest3.toString());
        assertEquals("| RequestType = SET_FLOOR_LAMPS id = 6; floorNumber = 3; direction = DOWN; state = true |", systemRequest4.toString());

        // Test serialize and deserialize methods
        byte[] serializedRequest = SystemRequest.serializeRequest(systemRequest1);
        SystemRequest deserializedRequest = SystemRequest.deserializeRequest(serializedRequest);
        assertEquals(systemRequest1.getId(), deserializedRequest.getId());
        assertEquals(systemRequest1.getType(), deserializedRequest.getType());
    }

    /**
     * Tests the SystemRequestType enum for correct string representations.
     */
    @Test
    public void testSystemRequestType() {
        assertEquals("REGISTER_ELEVATOR_CONTROLLER", SystemRequestType.REGISTER_ELEVATOR_CONTROLLER.name());
        assertEquals("NEW_PRIMARY_REQUEST", SystemRequestType.NEW_PRIMARY_REQUEST.name());
        assertEquals("IS_STOP_REQUIRED", SystemRequestType.IS_STOP_REQUIRED.name());
        assertEquals("PROCESSES_REQUESTS_AT_CURRENT_FLOOR", SystemRequestType.PROCESSES_REQUESTS_AT_CURRENT_FLOOR.name());
        assertEquals("PROCESS_COMPLETED_REQUESTS", SystemRequestType.PROCESS_COMPLETED_REQUESTS.name());
        assertEquals("SET_FLOOR_DIRECTION_LAMPS", SystemRequestType.SET_FLOOR_DIRECTION_LAMPS.name());
        assertEquals("SET_FLOOR_LAMPS", SystemRequestType.SET_FLOOR_LAMPS.name());
        assertEquals("ADD_NEW_REQUEST", SystemRequestType.ADD_NEW_REQUEST.name());
    }

    /**
     * Tests the UDPSenderReceiver class for sending and receiving system requests.
     */
    @Test
    public void testUDPSenderReceiver() {
        // Create a UDPSenderReceiver instance for testing
        UDPSenderReceiver udpSenderReceiver = new UDPSenderReceiver(0, 50000);

        // Test sending and receiving a system request
        SystemRequest request = new SystemRequest(SystemRequestType.ADD_NEW_REQUEST);
        udpSenderReceiver.sendSystemRequest(request);

        SystemRequest receivedRequest = udpSenderReceiver.receiveSystemRequest();

        assertNotNull(receivedRequest);
        assertEquals(SystemRequestType.ADD_NEW_REQUEST, receivedRequest.getType());
    }
}
