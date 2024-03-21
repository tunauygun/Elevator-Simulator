package Scheduler;

import Common.*;
import Floor.FloorSubsystem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static Common.SystemRequestType.*;

/**
 * Scheduler.Scheduler.java
 * <p>
 * The Scheduler class contains event queues and synchronizes the Elevator and Floor threads
 * by providing synchronized methods for writing and reading to event queues.
 *
 * @version 3.0, March 17, 2024
 */
public class Scheduler implements Runnable {

    private FloorSubsystem floorSubsystems;
    private int[] elevatorPorts = new int[Constants.NUMBER_OF_ELEVATORS];

    private UDPSenderReceiver senderReceiver1;
    private UDPSenderReceiver senderReceiver2;

    public Scheduler() {
        this.senderReceiver1 = new UDPSenderReceiver(Constants.SCHEDULER_PORT, 0);
        this.senderReceiver2 = new UDPSenderReceiver(Constants.SCHEDULER_PORT_2, 0);
    }

    /**
     * Sets the floor subsystem that coordinates all floors
     *
     * @param floorSubsystems The floor subsystem
     */
    public void setFloorSubsystems(FloorSubsystem floorSubsystems) {
        this.floorSubsystems = floorSubsystems;
    }

    /**
     * Adds a new elevator the scheduler
     *
     * @param elevatorPort The new elevator
     */
    public void addElevator(int elevatorId, int elevatorPort) {
        this.elevatorPorts[elevatorId] = elevatorPort;
    }

    /**
     * Continuously receives and processes system requests from the elevators and floors.
     */
    @Override
    public void run() {
        while (true) {
            System.out.println("Scheduler waiting for a system request.");
            SystemRequest request = senderReceiver1.receiveSystemRequest();
            int id = request.getId();
            int senderPort = senderReceiver1.getLastSenderPort();
            switch (request.getType()) {
                case ADD_NEW_REQUEST -> {
                    System.out.println("Received new request from floor " + request.getFloorNumber());
                    int bestElevatorId = selectBestElevatorNumber(request.getElevatorRequest());
                    senderReceiver2.sendSystemRequest(new SystemRequest(ADD_NEW_REQUEST, request.getElevatorRequest(), 0), elevatorPorts[bestElevatorId]);
                    System.out.println("Assigning the request to Elevator " + bestElevatorId);
                }
                case PROCESS_COMPLETED_REQUESTS -> {
                    LogPrinter.print(request.getId(), "Received new PROCESS_COMPLETED_REQUESTS request from Elevator " + request.getId());
                    senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                }
                case PROCESSES_REQUESTS_AT_CURRENT_FLOOR -> {
                    LogPrinter.print(request.getId(), "Received new PROCESSES_REQUESTS_AT_CURRENT_FLOOR request from Elevator " + request.getId());
                    senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                }
                case NEW_PRIMARY_REQUEST -> {
                    LogPrinter.print(request.getId(), "Elevator " + request.getId() + " is asking for a new primary request");
                    senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                    byte[] elevatorRequest = senderReceiver2.receiveResponse();
                    senderReceiver1.sendResponse(elevatorRequest, senderPort);
                    LogPrinter.print(request.getId(), "Sending Elevator " + request.getId() + " its new primary request");
                }
                case IS_STOP_REQUIRED -> {
                    LogPrinter.print(request.getId(), "Elevator " + request.getId() + " asking if a stop is required at floor " + request.getFloorNumber());
                    senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                    byte[] isRequired = senderReceiver2.receiveResponse();
                    senderReceiver1.sendResponse(isRequired, senderPort);
                    LogPrinter.print(request.getId(), "Replying to Elevator " + request.getId() + "'s IS_STOP_REQUIRED request");
                }
                case REGISTER_ELEVATOR_CONTROLLER -> {
                    this.addElevator(request.getId(), senderPort);
                    LogPrinter.print(request.getId(), "Registering Elevator " + request.getId() + " at port " + senderPort);
                }
                case SET_FLOOR_LAMPS -> {
                    LogPrinter.print(request.getId(), "Received SET_FLOOR_LAMPS request. Forwarding it to Floor Controller");
                    senderReceiver2.sendSystemRequest(request, Constants.FLOOR_CONTROLLER_PORT);
                }
                case SET_FLOOR_DIRECTION_LAMPS -> {
                    LogPrinter.print(request.getId(), "Received SET_FLOOR_DIRECTION_LAMPS request. Forwarding it to Floor Controller");
                    senderReceiver2.sendSystemRequest(request, Constants.FLOOR_CONTROLLER_PORT);
                }
            }
        }
    }

    /**
     * Gets the id of the elevator that is better suited to serve a given request
     *
     * @param elevatorRequest The elevator request to be assigned
     * @return The id of the best elevator to serve the request
     */
    private int selectBestElevatorNumber(ElevatorRequest elevatorRequest) {
        // Get the status of all elevators
        ArrayList<ElevatorStatus> elevatorStatuses = new ArrayList<>();
        for (int elevatorPort : elevatorPorts) {
            senderReceiver2.sendSystemRequest(new SystemRequest(STATUS_REQUEST), elevatorPort);
            ElevatorStatus elevatorStatus = ElevatorStatus.deserializeStatus(senderReceiver2.receiveResponse());
            elevatorStatuses.add(elevatorStatus);
        }

        // Check if there is an elevator that is already planned to stop at the floor
        for (ElevatorStatus e : elevatorStatuses) {
            if (e.getStopRequestFloorsInDirection(elevatorRequest.getDirection()).contains(elevatorRequest.getFloor())) {
                return e.getElevatorId();
            }
        }

        // Get a list of all idle elevators
        ArrayList<Integer> idleElevators = new ArrayList<>();
        for (int i = 0; i < elevatorStatuses.size(); i++) {
            if (elevatorStatuses.get(i).getDirection() == Direction.STOPPED) {
                idleElevators.add(i);
            }
        }

        // Get the list of candidate elevators for the request
        List<Integer> candidateElevators;
        if (!idleElevators.isEmpty()) {
            candidateElevators = idleElevators;
        } else {
            candidateElevators = IntStream.range(0, elevatorPorts.length).boxed().collect(Collectors.toList());
        }

        // Find the closest elevator
        int bestElevatorIndex = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int i : candidateElevators) {
            ElevatorStatus elevator = elevatorStatuses.get(i);
            int distance = Math.abs(elevator.getFloorNumber() - elevatorRequest.getFloor());

            if (distance < minDistance) {
                minDistance = distance;
                bestElevatorIndex = i;
            }
        }

        return bestElevatorIndex;
    }
}
