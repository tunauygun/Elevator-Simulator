import Common.Direction;
import Common.ElevatorRequest;
import Common.FaultType;
import Floor.*;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FloorTest.java
 * <p>
 * JUnit tests for the classes in the Floor package.
 *
 * @version 1.0, March 17, 2024
 */
public class FloorTest {
    /**
     * Tests the functionality of the Floor class.
     */
    @Test
    public void testFloor() {
        // Create Floor object for testing
        Floor floor = new Floor(1, 5);

        // Testing adding a new request and checking for it
        ElevatorRequest request = new ElevatorRequest(LocalTime.now(), 1, "up", 3, FaultType.NO_FAULT);
        floor.addRequest(request);

        ElevatorRequest processedRequest = floor.checkForRequests();

        assertEquals(request, processedRequest);

        // Testing setting the direction lamp
        floor.setDirectionLamp(1, Direction.UP, true);
        assertTrue(floor.getDirectionLamps()[1][0]);

        floor.setDirectionLamp(2, Direction.DOWN, false);
        assertFalse(floor.getDirectionLamps()[2][0]);

        // Testing setting the floor lamp
        floor.setFloorLamp(Direction.UP, true);
        assertTrue(floor.getFloorLampUp());

        floor.setFloorLamp(Direction.DOWN, true);
        assertFalse(floor.getFloorLampDown());
    }
}
