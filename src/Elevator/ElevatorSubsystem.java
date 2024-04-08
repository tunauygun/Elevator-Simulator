package Elevator;

import static Common.Constants.*;

import Common.Direction;
import Common.ElevatorRequest;
import Common.LogPrinter;
import Common.RequestStatus;
import Common.FaultType;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * ElevatorSubsystem.java
 * <p>
 * Represents the elevator subsystem responsible for managing elevator requests and state transitions.
 *
 * @version 1.0, March 17, 2024
 */
public class ElevatorSubsystem {
    //Queue for elevator events
    private ArrayList<ElevatorRequest> downRequests = new ArrayList<>();
    private ArrayList<ElevatorRequest> upRequests = new ArrayList<>();
    private boolean hasWaitingRequests;
    public ArrayList<Boolean> elevatorLamps = new ArrayList<>();

    private final int elevatorId;

    /**
     * Constructs a new ElevatorSubsystem instance.
     *
     * @param elevatorId The unique identifier for this elevator subsystem.
     */
    public ElevatorSubsystem(int elevatorId) {
        this.elevatorId = elevatorId;
        this.hasWaitingRequests = false;

        // Initialize all elevator laps at off state
        for (int i = 0; i < NUMBER_OF_FLOORS; i++) {
            elevatorLamps.add(false);
        }
    }

    /**
     * Gets a list of floor numbers where a request in the given direction is present
     *
     * @param direction The direction of travel
     * @return The floor number list
     */
    public synchronized ArrayList<Integer> getStopRequestFloorsInDirection(Direction direction) {
        ArrayList<Integer> floors = new ArrayList<>();

        ArrayList<ElevatorRequest> requests = direction == Direction.UP ? upRequests : downRequests;
        for (ElevatorRequest r : requests) {
            if (r.getStatus() == RequestStatus.PENDING) {
                floors.add(r.getFloor());
            }
        }
        return floors;
    }

    /**
     * Add the new assigned elevator request to the elevator's request queue
     *
     * @param request The new elevator request
     */
    public synchronized void addNewRequest(ElevatorRequest request) {
        switch (request.getDirection()) {
            case UP -> upRequests.add(request);
            case DOWN -> downRequests.add(request);
        }
        hasWaitingRequests = true;
    }

    /**
     * Returns the state of the request queue.
     *
     * @return True if there is a request in the queue, false otherwise.
     */
    public synchronized boolean hasWaitingRequests() {
        return hasWaitingRequests;
    }

    /**
     * Sets the state of elevator lamps for a specific floor.
     *
     * @param floorNumber The floor number.
     * @param lampState   The state of the lamp (true for on, false for off).
     */
    public synchronized void setElevatorLamps(int floorNumber, boolean lampState) {
        this.elevatorLamps.set(floorNumber - 1, lampState);
        if (lampState) {
            LogPrinter.print(this.elevatorId, "Elevator " + this.elevatorId + ": Turned on elevator button " + floorNumber);
        } else {
            LogPrinter.print(this.elevatorId, "Elevator " + this.elevatorId + ": Turned off elevator button " + floorNumber);
        }
    }


    /**
     * Check all request in the queue for the given direction. If the request is completed by stopping at the given
     * floor number, the request is completed and removed form the queue
     *
     * @param floorNumber The floor number of the elevator stop
     * @param direction   The direction of travel of the elevator
     */
    public synchronized void processCompletedRequests(int floorNumber, Direction direction) {
        if (direction == Direction.UP) {
            // Iterate over all up requests
            Iterator<ElevatorRequest> iterator = this.upRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();

                // If the request is completed, remove from the queue and update elevator lamps
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    LogPrinter.print(this.elevatorId, "Elevator " + this.elevatorId + ": Completed Request: " + r);
                    this.setElevatorLamps(r.getCarButton(), false);
                    iterator.remove();
                }
            }
        } else {
            // Iterate over all down requests
            Iterator<ElevatorRequest> iterator = this.downRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();

                // If the request is completed, remove from the queue and update elevator lamps
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    LogPrinter.print(this.elevatorId, "Elevator " + this.elevatorId + ": Completed Request: " + r);
                    this.setElevatorLamps(r.getCarButton(), false);
                    iterator.remove();
                }
            }
        }

        // Update the flag for empty elevator request queue
        if (this.upRequests.isEmpty() && this.downRequests.isEmpty()) {
            this.hasWaitingRequests = false;
        }
    }


    /**
     * Process the request at the given floor for the given direction by picking up the
     * passengers/elevator requests and updating the elevator lamps.
     *
     * @param floorNumber The floor number
     * @param direction   The elevator direction of travel
     */
    public synchronized void processRequestsAtCurrentFloor(int floorNumber, Direction direction) {
        if (direction == Direction.UP) {
            for (ElevatorRequest r : this.upRequests) {
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PENDING) {
                    r.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.setElevatorLamps(r.getCarButton(), true);
                    LogPrinter.print(this.elevatorId, "Elevator " + this.elevatorId + ": Picked up passenger: " + r);
                }
            }
        } else {
            for (ElevatorRequest r : this.downRequests) {
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PENDING) {
                    r.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.setElevatorLamps(r.getCarButton(), true);
                    LogPrinter.print(this.elevatorId, "Elevator " + this.elevatorId + ": Picked up passenger: " + r);
                }
            }
        }
    }


    /**
     * Return a new primary request for the elevator from the request queue. This method prioritizes
     * elevator requests that are partially processed (which means that the elevator picked up the passenger
     * but still didn't stop at their destination floor). If there are no partially completed requests, returns
     * any available request. If the request queue is empty, returns null.
     *
     * @return A new primary elevator request
     */
    public synchronized ElevatorRequest receiveNewPrimaryRequest() {

        // Check if elevator request queue is empty
        if (!this.hasWaitingRequests) {
            return null;
        }

        // If there is only one request left, set hasWaitingRequests
        // since the last remaining request will be removed by the end of this method
        if (this.upRequests.size() + this.downRequests.size() == 1) {
            this.hasWaitingRequests = false;
        }

        // Check for partially processed request in UP direction
        for (int i = 0; i < this.upRequests.size(); i++) {
            if (this.upRequests.get(i).getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                return this.upRequests.remove(i);
            }
        }

        // Check for partially processed request in DOWN direction
        for (int i = 0; i < this.downRequests.size(); i++) {
            if (this.downRequests.get(i).getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                return this.downRequests.remove(i);
            }
        }

        // Since there are no partially completed request, check for any request in UP direction
        if (!this.upRequests.isEmpty()) {
            return this.upRequests.remove(0);
        }

        // Since there are no UP requests, return any request in DOWN direction
        return this.downRequests.remove(0);
    }


    /**
     * Checks of a stop is required at any given floor for the given direction
     *
     * @param nextFloorNumber The floor number
     * @param direction       The direction of travel
     * @return True if a stop is required, false otherwise
     */
    public synchronized boolean isStopRequiredForFloor(int nextFloorNumber, Direction direction) {
        if (direction == Direction.UP) {
            // Check for requests in the up direction
            for (ElevatorRequest r : this.upRequests) {
                if (r.getCurrentTargetFloor() == nextFloorNumber) {
                    return true;
                }
            }
            return false;
        }

        // Check for request in the down direction
        for (ElevatorRequest r : this.downRequests) {
            if (r.getCurrentTargetFloor() == nextFloorNumber) {
                return true;
            }
        }
        return false;
    }

    public boolean hasFault(FaultType faultType, int floorNumber) {
        for (ElevatorRequest e : upRequests) {
            if (e.getStatus() == RequestStatus.PASSENGER_PICKED_UP && e.getFault() == faultType && e.getCarButton() == floorNumber) {
                return true;
            }
        }

        for (ElevatorRequest e : downRequests) {
            if (e.getStatus() == RequestStatus.PASSENGER_PICKED_UP && e.getFault() == faultType && e.getCarButton() == floorNumber) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<ElevatorRequest> getWaitingRequests() {
        ArrayList<ElevatorRequest> requestsNotPicked = new ArrayList<>();

        for (ElevatorRequest e : upRequests) {
            if (e.getStatus() != RequestStatus.PASSENGER_PICKED_UP) {
                requestsNotPicked.add(e);
            }
        }

        for (ElevatorRequest e : downRequests) {
            if (e.getStatus() != RequestStatus.PASSENGER_PICKED_UP) {
                requestsNotPicked.add(e);
            }
        }
        return requestsNotPicked;
    }

    public ArrayList<ElevatorRequest> getUpRequests() {
        return this.getUpRequests();
    }

    public ArrayList<ElevatorRequest> getDownRequests() {
        return this.getDownRequests();
    }
}
