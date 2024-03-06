import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the Elevator class.
 *
 * @version 2.0 February 24, 2024
 */
public class ElevatorTest {

    /**
     * Test case to verify the creation of an Elevator instance.
     * This test checks if the Elevator object is initialized correctly.
     */
    @Test
    public void testElevatorCreation() {
        Scheduler scheduler = new Scheduler();
        int numberOfFloors = 10;

        Elevator elevator = new Elevator(scheduler, numberOfFloors);

        assertNotNull(elevator);
        assertEquals(Direction.STOPPED, elevator.getDirection());
        assertEquals(1, elevator.getFloorNumber());
        assertFalse(elevator.isMotorRunning());
        assertTrue(elevator.isDoorOpen());
        assertFalse(elevator.hasWaitingRequests());
    }
}

