package Floor;

import Common.Constants;

public class Main {
    public static void main(String[] args) {
        FloorSubsystem flo = new FloorSubsystem("data.txt", Constants.NUMBER_OF_FLOORS);
        Thread floorSubsystem = new Thread(flo, "Floor");

        // Start the elevator and floor thread
        floorSubsystem.start();
    }
}
