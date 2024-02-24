import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * Floor.java
 * <p>
 * The Floor models a floor in a building. For iterable 1, the floor class reads the elevator
 * events from the input file, sends/receives them to/from the scheduler.
 *
 * @version 2.0, February 22, 2024
 */
public class FloorSubsystem implements Runnable {
    private Scheduler scheduler;
    private Random random = new Random();
    private int numberOfFloors;

    // Buffer that hold the events to be read by floor
    private ArrayList<Floor> floors = new ArrayList<>();

    // True if there is at least one event to read
    private boolean eventAvailableForFloor = false;

    /**
     * Constructs an instance of the Floor class
     *
     * @param scheduler     The scheduler that synchronizes the floor with the elevator
     * @param inputFileName The name of the input file that contains the elevator event data
     */
    public FloorSubsystem(Scheduler scheduler, String inputFileName, int numberOfFloors) {
        this.scheduler = scheduler;
        this.numberOfFloors = numberOfFloors;
        for (int i = 0; i < numberOfFloors; i++) {
            floors.add(new Floor(i + 1, numberOfFloors));
        }
        for (ElevatorRequest e : readInputFile(inputFileName)) {
            floors.get(e.getFloor() - 1).addRequest(e);
        }
    }

    private ArrayList<ElevatorRequest> readInputFile(String fileName) {
        File dataFile = new File(fileName);
        ArrayList<ElevatorRequest> events = new ArrayList<>();

        try {
            Scanner myReader = new Scanner(dataFile);

            // Read the file line by line and send the elevator events to the scheduler
            while (myReader.hasNextLine()) {

                // Parse the input file line to elevator event
                String[] data = myReader.nextLine().strip().split(" ");
                LocalTime time = LocalTime.parse(data[0]);

                ElevatorRequest event = new ElevatorRequest(time, Integer.parseInt(data[1]), data[2], Integer.parseInt(data[3]));

                events.add(event);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public void run() {
        while (true) {
            for (Floor f : floors) {
                ElevatorRequest request = f.checkForRequests();
                if (request != null) {
                    scheduler.sendNewRequest(request);
                }
            }
        }
    }

    public void setDirectionLamp(int floorNumber, Direction direction, boolean state) {
        this.floors.get(floorNumber - 1).setDirectionLamp(direction, state);
    }

    public void setFloorLamp(int floorNumber, Direction direction, boolean state) {
        this.floors.get(floorNumber - 1).setFloorLamp(direction, state);
    }
}
