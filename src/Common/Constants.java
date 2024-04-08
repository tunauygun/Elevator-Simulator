package Common;

/**
 * Constants.java
 * <p>
 * This class defines constants related to an elevator control system.
 * It includes values for the number of floors, elevators, port numbers,
 * and time intervals.
 *
 * @version 1.0, March 17, 2024
 */
public final class Constants {
    public static final int NUMBER_OF_FLOORS = 22;
    public static final int NUMBER_OF_ELEVATORS = 4;
    public static final int MAX_PASSENGER_COUNT = 5;

    // Port Numbers
    public static final int SCHEDULER_PORT = 50000;
    public static final int SCHEDULER_PORT_2 = 50001;
    public static final int FLOOR_CONTROLLER_PORT = 50002;

    // Travel and loading times
    public static final int BASE_MOVE_TIME = 0;
    public static final int INCREMENTAL_MOVE_TIME = 10000;
    public static final int LOADING_TIME = 6000;
    public static final int BOARDING_TIME_PER_PASSENGER = 5000;
    public static final int TRANSIENT_FAULT_TIME = 20000;
}
