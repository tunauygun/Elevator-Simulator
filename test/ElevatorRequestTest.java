import org.junit.Test;
import java.time.LocalTime;
import static org.junit.Assert.*;

/**
 * Test class for the ElevatorRequest class.
 *
 * @version 1.0, February 24, 2024
 */
public class ElevatorRequestTest {

    /**
     * Test case to verify the creation of an ElevatorRequest object.
     */
    @Test
    public void testElevatorRequestCreation() {
        LocalTime time = LocalTime.of(12, 0);

        ElevatorRequest request = new ElevatorRequest(time, 5, "up", 3);

        assertEquals(time, request.getTime());
        assertEquals(5, request.getFloor());
        assertEquals(Direction.DOWN, request.getDirection());
        assertEquals(3, request.getCarButton());
        assertEquals(RequestStatus.PENDING, request.getStatus());
    }
}
