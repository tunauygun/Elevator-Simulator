package Elevator;

import static Common.Constants.*;
import Common.Direction;
import Common.ElevatorRequest;
import Common.RequestStatus;

import java.util.ArrayList;
import java.util.Iterator;

public class ElevatorSubsystem {
    //Queue for elevator events
    private ArrayList<ElevatorRequest> downRequests = new ArrayList<>();
    private ArrayList<ElevatorRequest> upRequests = new ArrayList<>();
    private boolean hasWaitingRequests;
    public ArrayList<Boolean> elevatorLamps = new ArrayList<>();

    public ElevatorSubsystem() {
        this.hasWaitingRequests = false;

        // Initialize all elevator laps at off state
        for (int i = 0; i < NUMBER_OF_FLOORS; i++) {
            elevatorLamps.add(false);
        }
    }

    /**
     * Add the new assigned elevator request to the elevator's request queue
     * @param request The new elevator request
     */
    public synchronized void addNewRequest(ElevatorRequest request) {
        switch (request.getDirection()) {
            case Direction.UP -> upRequests.add(request);
            case Direction.DOWN -> downRequests.add(request);
        }
        hasWaitingRequests = true;
    }

    /**
     * Returns the state of the request queue.
     * @return True if there is a request in the queue, false otherwise.
     */
    public synchronized boolean hasWaitingRequests() {
        return hasWaitingRequests;
    }


    public synchronized void setElevatorLamps(int floorNumber, boolean lampState){
        this.elevatorLamps.set(floorNumber, lampState);
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
            Iterator<ElevatorRequest> iterator = this.upRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();

                // If the request is completed, remove from the queue and update elevator lamps
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed Request: " + r);
                    this.elevatorLamps.set(r.getCarButton(), false);
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
                    System.out.println("Completed Request: " + r);
                    this.elevatorLamps.set(r.getCarButton(), false);
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
     * @param floorNumber The floor number
     * @param direction The elevator direction of travel
     */
    public synchronized void processRequestsAtCurrentFloor(int floorNumber, Direction direction) {
        if (direction == Direction.UP) {
            for (ElevatorRequest r : this.upRequests) {
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PENDING) {
                    r.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.elevatorLamps.set(r.getCarButton() - 1, true);
                    System.out.println("Picked up passenger: " + r);
                }
            }
        } else {
            for (ElevatorRequest r : this.downRequests) {
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PENDING) {
                    r.setStatus(RequestStatus.PASSENGER_PICKED_UP);
                    this.elevatorLamps.set(r.getCarButton() - 1, true);
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
        if (!this.hasWaitingRequests) {
            return null;
            //TODO: WAIT
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


    /** Checks of a stop is required at any given floor for the given direction
     * @param nextFloorNumber The floor number
     * @param direction The direction of travel
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


}
