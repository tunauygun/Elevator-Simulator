/**
 * Main.java
 * <p>
 * The Main class contains the main method and crates the scheduler
 * elevator, and floor classes.
 *
 * @version 2.0, February 22, 2024
 */
public class Main {
    public static void main(String[] args) {

        final int NUMBER_OF_FLOORS = 10;

        // Instantiate the scheduler, elevator, and floor instances
        Scheduler scheduler = new Scheduler();

        //Instantiate Elevator and Floor
        Elevator elev = new Elevator(scheduler, NUMBER_OF_FLOORS);
        FloorSubsystem flo = new FloorSubsystem(scheduler, "data.txt", NUMBER_OF_FLOORS);

        Thread elevator = new Thread(elev, "Elevator");
        Thread floorSubsystem = new Thread(flo, "Floor");

        // Add Elevator to scheduler
        scheduler.addElevator(elev);
        scheduler.setFloorSubsystems(flo);

        // Start the elevator and floor thread
        elevator.start();
        floorSubsystem.start();
    }
}
