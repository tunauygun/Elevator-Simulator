import org.junit.Test;
import java.time.LocalTime;
import static org.junit.Assert.*;

/**
 * Test class for the Floor class.
 *
 * @version 2.0 February 24, 2024
 */
public class FloorTest {

    /**
     * Test case to verify adding a request to the floor and checking for requests.
     */
    @Test
    public void testAddRequestAndCheckForRequests() {
        Floor floor = new Floor(1, 10);
        LocalTime currentTime = LocalTime.now();
        ElevatorRequest request = new ElevatorRequest(currentTime, 1, "up", 3);

        floor.addRequest(request);
        ElevatorRequest retrievedRequest = floor.checkForRequests();

        assertEquals(request, retrievedRequest);
    }
}
