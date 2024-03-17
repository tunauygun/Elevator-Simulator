package Floor;

import Common.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * FloorSubsystem.java
 * <p>
 * The FloorSystem coordinates between individual floors and the elevator in a building. The floor subsystem
 * reads the elevator requests from the input file, forwards lamp commands scheduler to floors, sends the
 * elevator request from individual floors to the scheduler.
 *
 * @version 3.0, March 17, 2024
 */
public class FloorSubsystem implements Runnable {

    private UDPSenderReceiver sender;

    private int numberOfFloors;

    // List of all floors managed by the floor subsystem
    private ArrayList<Floor> floors = new ArrayList<>();

    /**
     * Constructs an instance of the Floor.FloorSubsystem class
     *
     * @param inputFileName  The name of the input file that contains the elevator event data
     * @param numberOfFloors Total number of floors in the building
     */
    public FloorSubsystem(String inputFileName, int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
        this.sender = new UDPSenderReceiver(0, Constants.SCHEDULER_PORT);

        // Instantiates all floors in the building
        for (int i = 0; i < numberOfFloors; i++) {
            floors.add(new Floor(i + 1, numberOfFloors));
        }

        // Adds the elevator requests from the file to their corresponding floor
        for (ElevatorRequest e : readInputFile(inputFileName)) {
            floors.get(e.getFloor() - 1).addRequest(e);
        }
    }

    /**
     * Reads an input file containing the elevator requests and returns the requests as a list
     *
     * @param fileName The filename for the input file
     * @return List of elevator requests
     */
    private ArrayList<ElevatorRequest> readInputFile(String fileName) {
        File dataFile = new File(fileName);
        ArrayList<ElevatorRequest> events = new ArrayList<>();

        //Get the Current Time
        LocalTime currentTime = LocalTime.now();

        try {
            Scanner myReader = new Scanner(dataFile);

            // Read the file line by line and send the elevator events to the scheduler
            while (myReader.hasNextLine()) {

                // Parse the input file line to elevator event
                String[] data = myReader.nextLine().strip().split(" ");
                LocalTime offset = LocalTime.parse(data[0]);

                //Take the Offset Time found in the file and add it to the current time to get the time of the event
                LocalTime time = currentTime.plusHours(offset.getHour()).plusMinutes(offset.getMinute()).plusSeconds(offset.getSecond());

                ElevatorRequest event = new ElevatorRequest(time, Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]));

                events.add(event);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Sets the direction lamp at the given floor for the given direction to the given state
     *
     * @param floorNumber The floor number where the lamp is located
     * @param direction   The direction of the lamp
     * @param state       The target state of the lamp. (on/off)
     */
    public void setDirectionLamp(int elevatorId, int floorNumber, Direction direction, boolean state) {
        this.floors.get(floorNumber - 1).setDirectionLamp(elevatorId, direction, state);
        System.out.println("Set floor direction lamp: ElevatorId = " + elevatorId + " Direction = " + direction + " State = " + (state ? "ON" : "OFF"));
    }

    /**
     * Sets the floor lamp at the given floor for the given direction to the given state
     *
     * @param floorNumber The floor number where the lamp is located
     * @param direction   The direction of the lamp
     * @param state       The target state of the lamp. (on/off)
     */
    public void setFloorLamp(int floorNumber, Direction direction, boolean state) {
        this.floors.get(floorNumber - 1).setFloorLamp(direction, state);
        System.out.println("Set floor lamp: Direction = " + direction + " State = " + (state ? "ON" : "OFF"));
    }


    /**
     * Keeps checking the floors for new elevator request and sends any request
     * received at a floor to the scheduler to be assigned to an elevator
     */
    @Override
    public void run() {
        while (true) {
            for (Floor f : floors) {
                ElevatorRequest request = f.checkForRequests();
                if (request != null) {
                    SystemRequest sr = new SystemRequest(SystemRequestType.ADD_NEW_REQUEST, request, 0);
                    sender.sendSystemRequest(sr, Constants.SCHEDULER_PORT);
                    System.out.println("Sending new request to the scheduler: " + request);

                    this.setFloorLamp(request.getFloor(), request.getDirection(), true);
                }
            }
        }
    }
}
