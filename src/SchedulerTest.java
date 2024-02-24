import org.junit.Test;
import java.time.LocalTime;
import static org.junit.Assert.*;

/**
 * Test class for the Scheduler class.
 *
 * @version 2.0 February 24, 2024
 */
public class SchedulerTest {

    /**
     * Test case to verify sending a new request and receiving the first primary request.
     */
    @Test
    public void testSendNewRequestAndReceiveFirstPrimaryRequest() {
        Scheduler scheduler = new Scheduler();
        Elevator elevator = new Elevator(scheduler, 10);
        scheduler.addElevator(elevator);
        LocalTime currentTime = LocalTime.now();
        ElevatorRequest request = new ElevatorRequest(currentTime, 1, "up", 3);

        scheduler.sendNewRequest(request);
        ElevatorRequest receivedRequest = scheduler.receiveFirstPrimaryRequest();

        assertEquals(request, receivedRequest);
    }
}
