import Floor.FloorSubsystem;
import Scheduler.Scheduler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SchedulerTest.java
 * <p>
 * JUnit tests for the classes in the Scheduler package.
 *
 * @version 1.0, March 17, 2024
 */
public class SchedulerTest {
    /**
     * Tests the functionality of the Scheduler class.
     */
    @Test
    public void testScheduler() {
        // Create a Scheduler instance
        Scheduler scheduler = new Scheduler();

        // Test adding elevators
        scheduler.addElevator(1, 1234);
        scheduler.addElevator(2, 5678);

        // Test setting the floor subsystem
        FloorSubsystem floorSubsystem = new FloorSubsystem("data.txt", 10);
        scheduler.setFloorSubsystems(floorSubsystem);

        // Test the run method (assuming it runs indefinitely)
        Thread thread = new Thread(scheduler);
        thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(thread.isAlive());
    }
}
