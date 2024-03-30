import Common.*;
import Elevator.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ElevatorTest.java
 * <p>
 * JUnit tests for the classes in the Elevator package.
 *
 * @version 1.0, March 17, 2024
 */
public class ElevatorTest {
    /**
     * Tests the functionality of the Elevator class.
     */
    @Test
    public void testElevator() {

        // Create Elevator and ElevatorSubsystem objects for testing
        ElevatorSubsystem subsystem = new ElevatorSubsystem(1);
        Elevator elevator = new Elevator(subsystem, 1, false);
        ElevatorState closeDoorState = new CloseDoorState(elevator);

        // Test Elevator Construction
        assertNotNull(elevator);
        assertInstanceOf(IdleState.class, elevator.getCurrentState());
        assertEquals(Direction.STOPPED, elevator.getDirection());
        assertEquals(1, elevator.getFloorNumber());
        assertTrue(elevator.isDoorOpen());
        assertFalse(elevator.isMotorRunning());

        // Test processing the ElevatorState
        ElevatorRequest request = new ElevatorRequest(LocalTime.now(), 1, "Up", 2, FaultType.NO_FAULT);
        elevator.setPrimaryRequest(request);

        assertInstanceOf(IdleState.class, elevator.getCurrentState());

        elevator.getCurrentState().handleState();
        assertInstanceOf(CloseDoorState.class, elevator.getCurrentState());

        // Test getting the floor number
        assertEquals(1, elevator.getFloorNumber());

        elevator.getCurrentState().handleState();
        assertInstanceOf(MovingState.class, elevator.getCurrentState());

        elevator.getCurrentState().handleState();
        assertInstanceOf(OpenDoorState.class, elevator.getCurrentState());

        assertEquals(2, elevator.getFloorNumber());

        assertEquals(3, elevator.getNextFloorNumber());

    }

    /**
     * Tests the functionality of the ElevatorSubsystem class.
     */
    @Test
    public void testElevatorSubsystem() {
        // Create new ElevatorSubsystem object for testing
        ElevatorSubsystem subsystem = new ElevatorSubsystem(1);

        // Testing adding a new request
        ElevatorRequest request = new ElevatorRequest(LocalTime.now(), 1, "up", 2, FaultType.NO_FAULT);
        subsystem.addNewRequest(request);
        assertTrue(subsystem.hasWaitingRequests());

        // Testing processing completed requests
        subsystem.processRequestsAtCurrentFloor(1, Direction.UP);
        subsystem.processCompletedRequests(2, Direction.UP);

        assertFalse(subsystem.hasWaitingRequests());

        // Testing receiving new primary requests
        ElevatorRequest request2 = new ElevatorRequest(LocalTime.now(),2, "up", 4, FaultType.NO_FAULT);
        subsystem.addNewRequest(request2);

        ElevatorRequest primaryRequest = subsystem.receiveNewPrimaryRequest();
        assertNotNull(primaryRequest);

        // Testing if a stop is required
        boolean stopRequired = subsystem.isStopRequiredForFloor(3, Direction.UP);
        assertFalse(stopRequired);
    }
}
