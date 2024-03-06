import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the FloorSubsystem class.
 *
 * @version 1.0 February 24, 2024
 */
public class FloorSubsystemTest {

    /**
     * Test case to verify the creation of a FloorSubsystem instance.
     * This test checks if the FloorSubsystem object is initialized correctly.
     */
    @Test
    public void testFloorSubsystemCreation() {
        Scheduler scheduler = new Scheduler();
        int numberOfFloors = 10;
        String inputFileName = "data.txt";

        FloorSubsystem floorSubsystem = new FloorSubsystem(scheduler, inputFileName, numberOfFloors);

        assertNotNull(floorSubsystem);
    }
}
