package Floor;

import Common.Constants;

/**
 * Main class for initializing and starting floor subsystems and controllers.
 *
 * @version 1.0, March 17, 2024
 */
public class Main {
    public static void main(String[] args) {
        FloorSubsystem flo = new FloorSubsystem("data.txt", Constants.NUMBER_OF_FLOORS);
        FloorController floCont = new FloorController(flo);
        Thread floorSubsystem = new Thread(flo, "FloorSubsystem");
        Thread floorController = new Thread(floCont, "FloorController");

        // Start the elevator and floor thread
        floorSubsystem.start();
        floorController.start();
    }
}
