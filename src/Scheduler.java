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

    public void setFloorSubsystems(FloorSubsystem floorSubsystems) {
        this.floorSubsystems = floorSubsystems;
    }

    public void addElevator(Elevator elevator) {
        this.elevators.add(elevator);
    }

    public synchronized void sendNewRequest(ElevatorRequest request) {
        this.elevators.get(0).addNewRequest(request);
        System.out.println("New Request Received: " + request);
        notifyAll();
    }

    public synchronized ElevatorRequest receiveFirstPrimaryRequest() {
        while (!this.elevators.get(0).hasWaitingRequests) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }

        if (!this.elevators.get(0).upRequests.isEmpty()) {
            return this.elevators.get(0).upRequests.remove(0);
        }

        return this.elevators.get(0).downRequests.remove(0);
    }

    public synchronized boolean isStopRequiredForFloor(int nextFloorNumber, Direction direction) {
        if (direction == Direction.UP) {
            for (ElevatorRequest r : this.elevators.get(0).upRequests) {
                if (r.getCurrentTargetFloor() == nextFloorNumber) {
                    return true;
                }
            }
            return false;
        }

        for (ElevatorRequest r : this.elevators.get(0).downRequests) {
            if (r.getCurrentTargetFloor() == nextFloorNumber) {
                return true;
            }
        }
        return false;
    }

    public synchronized void processCompletedRequests(int floorNumber, Direction direction) {
        if (direction == Direction.UP) {
            Iterator<ElevatorRequest> iterator = this.elevators.get(0).upRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed Request: " + r);
                    this.elevators.get(0).elevatorLamps.set(r.getCarButton(), false);
                    iterator.remove();
                }
            }
        } else {
            Iterator<ElevatorRequest> iterator = this.elevators.get(0).downRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest r = iterator.next();
                if (r.getCurrentTargetFloor() == floorNumber && r.getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                    System.out.println("Completed Request: " + r);
                    this.elevators.get(0).elevatorLamps.set(r.getCarButton(), false);
                    iterator.remove();
                }
            }
        }
        if (this.elevators.get(0).upRequests.isEmpty() && this.elevators.get(0).downRequests.isEmpty()) {
            this.elevators.get(0).hasWaitingRequests = false;
        }
    }

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

    public synchronized ElevatorRequest receiveNewPrimaryRequest() {
        if (!this.elevators.get(0).hasWaitingRequests) {
            return null;
        }

        for (int i = 0; i < this.elevators.get(0).upRequests.size(); i++) {
            if (this.elevators.get(0).upRequests.get(i).getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                return this.elevators.get(0).upRequests.remove(i);
            }
        }

        for (int i = 0; i < this.elevators.get(0).downRequests.size(); i++) {
            if (this.elevators.get(0).downRequests.get(i).getStatus() == RequestStatus.PASSENGER_PICKED_UP) {
                return this.elevators.get(0).downRequests.remove(i);
            }
        }

        if (!this.elevators.get(0).upRequests.isEmpty()) {
            return this.elevators.get(0).upRequests.remove(0);
        }

        return this.elevators.get(0).downRequests.remove(0);
    }

    public synchronized void setFloorDirectionLamp(int floorNumber, Direction direction, boolean state) {
        this.floorSubsystems.setDirectionLamp(floorNumber, direction, state);
    }

    public synchronized void setFloorLamp(int floorNumber, Direction direction, boolean state) {
        this.floorSubsystems.setFloorLamp(floorNumber, direction, state);
    }

}
