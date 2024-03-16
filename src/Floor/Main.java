package Floor;

import Common.Constants;

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
