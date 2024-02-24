import java.util.ArrayList;
import java.util.Iterator;

/**
 * Scheduler.java
 * <p>
 * The Scheduler class contains event queues and synchronizes the Elevator and Floor threads
 * by providing synchronized methods for writing and reading to event queues.
 *
 * @version 2.0, February 22, 2024
 */
public class Scheduler {

    //List of Elevators scheduler has access to
    private ArrayList<Elevator> elevators = new ArrayList<>();
    private FloorSubsystem floorSubsystems;

    /**
     * Sets the floor subsystem that coordinates all floors
     * @param floorSubsystems The floor subsystem
     */
    public void setFloorSubsystems(FloorSubsystem floorSubsystems) {
        this.floorSubsystems = floorSubsystems;
    }

    /**
     * Adds a new elevator the scheduler
     * @param elevator The new elevator
     */
    public void addElevator(Elevator elevator) {
        this.elevators.add(elevator);
    }

    /**
     * Sends the given elevator request to an available elevator
     * @param request The new elevator request
     */
    public synchronized void sendNewRequest(ElevatorRequest request) {
        this.elevators.get(0).addNewRequest(request);
        System.out.println("New Request Received: " + request);
        notifyAll();
    }

    /**
     * Returns a new elevator request to an elevator waiting at IDLE
     * @return New elevator request
     */
    public synchronized ElevatorRequest receiveFirstPrimaryRequest() {
        // Wait until there is an available request
        while (!this.elevators.get(0).hasWaitingRequests) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

        // Return the available request and remove from the queue
        if (!this.elevators.get(0).upRequests.isEmpty()) {
            return this.elevators.get(0).upRequests.remove(0);
        }
        return this.elevators.get(0).downRequests.remove(0);
    }

    /** Checks of a stop is required at any given floor for the given direction
     * @param nextFloorNumber The floor number
     * @param direction The direction of travel
     * @return True if a stop is required, false otherwise
     */
    public synchronized boolean isStopRequiredForFloor(int nextFloorNumber, Direction direction) {
        if (direction == Direction.UP) {
            // Check for requests in the up direction
            for (ElevatorRequest r : this.elevators.get(0).upRequests) {
                if (r.getCurrentTargetFloor() == nextFloorNumber) {
                    return true;
                }
            }
            return false;
        }

        // Check for request in the down direction
        for (ElevatorRequest r : this.elevators.get(0).downRequests) {
            if (r.getCurrentTargetFloor() == nextFloorNumber) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check all request in the queue for the given direction. If the request is completed by stoping at the given
     * floor number, the request is completed and removed form the queue
     * @param floorNumber The floor number of the elevator stop
     * @param direction The direction of travel of the elevator
     */
    public synchronized void processCompletedRequests(int floorNumber, Direction direction) {
        if (direction == Direction.UP) {
            // Iterate over all up requests
            Iterator<ElevatorRequest> iterator = this.elevators.get(0).upRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();

                // If the request is completed, remove from the queue and update elevator lamps
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed Request: " + r);
                    this.elevators.get(0).elevatorLamps.set(r.getCarButton(), false);
                    iterator.remove();
                }
            }
        } else {
            // Iterate over all down requests
            Iterator<ElevatorRequest> iterator = this.elevators.get(0).downRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();

                // If the request is completed, remove from the queue and update elevator lamps
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed Request: " + r);
                    this.elevators.get(0).elevatorLamps.set(r.getCarButton(), false);
                    iterator.remove();
                }
            }
        }

        // Update the flag for empty elevator request queue
        if (this.elevators.get(0).upRequests.isEmpty() && this.elevators.get(0).downRequests.isEmpty()) {
            this.elevators.get(0).hasWaitingRequests = false;
        }
    }

    /**
     * Process the request at the given floor for the given direction by picking up the
     * passengers/elevator requests and updating the elevator lamps.
     * @param floorNumber The floor number
     * @param direction The elevator direction of travel
     */
    public synchronized void processRequestsAtCurrentFloor(int floorNumber, Direction direction) {
        if (direction == Direction.UP) {
            for (ElevatorRequest r : this.elevators.get(0).upRequests) {
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PENDING) {
                    r.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.elevators.get(0).elevatorLamps.set(r.getCarButton() - 1, true);
                    System.out.println("Picked up passenger: " + r);
                }
            }
        } else {
            for (ElevatorRequest r : this.elevators.get(0).downRequests) {
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PENDING) {
                    r.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.elevators.get(0).elevatorLamps.set(r.getCarButton() - 1, true);
                    System.out.println("Picked up passenger: " + r);
                }
            }
        }
    }

    /**
     * Return a new primary request for the elevator from the request queue. This method prioritizes
     * elevator requests that are partially processed (which means that the elevator picked up the passenger
     * but still didn't stop at their destination floor). If there are no partianlly completed requests, returns
     * any available request. If the request queue is empty, returns null.
     * @return A new primary elevator request
     */
    public synchronized ElevatorRequest receiveNewPrimaryRequest() {

        // Check if elevator request queue is empty
        if (!this.elevators.get(0).hasWaitingRequests) {
            return null;
        }

        // Check for partially processed request in UP direction
        for (int i = 0; i < this.elevators.get(0).upRequests.size(); i++) {
            if (this.elevators.get(0).upRequests.get(i).getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                return this.elevators.get(0).upRequests.remove(i);
            }
        }

        // Check for partially processed request in DOWN direction
        for (int i = 0; i < this.elevators.get(0).downRequests.size(); i++) {
            if (this.elevators.get(0).downRequests.get(i).getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                return this.elevators.get(0).downRequests.remove(i);
            }
        }

        // Since there are no partially completed request, check for any request in UP direction
        if (!this.elevators.get(0).upRequests.isEmpty()) {
            return this.elevators.get(0).upRequests.remove(0);
        }

        // Since there are no UP requests, return any request in DOWN direction
        return this.elevators.get(0).downRequests.remove(0);
    }

    /**
     * Sets the direction lamp at the given floor for the given direction to the given state
     * @param floorNumber The floor number where the lamp is located
     * @param direction The direction of the lamp
     * @param state The target state of the lamp. (on/off)
     */
    public synchronized void setFloorDirectionLamp(int floorNumber, Direction direction, boolean state) {
        this.floorSubsystems.setDirectionLamp(floorNumber, direction, state);
    }

    /**
     * Sets the floor lamp at the given floor for the given direction to the given state
     * @param floorNumber The floor number where the lamp is located
     * @param direction The direction of the lamp
     * @param state The target state of the lamp. (on/off)
     */
    public synchronized void setFloorLamp(int floorNumber, Direction direction, boolean state) {
        this.floorSubsystems.setFloorLamp(floorNumber, direction, state);
    }

}
