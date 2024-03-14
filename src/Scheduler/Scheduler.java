package Scheduler;

import Common.Constants;
import Common.ElevatorRequest;
import Common.SystemRequest;
import Common.UDPSenderReceiver;
import Elevator.Elevator;
import Floor.FloorSubsystem;

import java.util.ArrayList;
import java.util.Iterator;

import static Common.SystemRequestType.*;

/**
 * Scheduler.Scheduler.java
 * <p>
 * The Scheduler.Scheduler class contains event queues and synchronizes the Elevator.Elevator and Floor.Floor threads
 * by providing synchronized methods for writing and reading to event queues.
 *
 * @version 2.0, February 22, 2024
 */
public class Scheduler implements Runnable {

    //List of Elevators scheduler has access to
    //private ArrayList<Elevator> elevators = new ArrayList<>();
    private FloorSubsystem floorSubsystems;
    private int[] elevatorPorts = new int[Constants.NUMBER_OF_ELEVATORS];

    private UDPSenderReceiver senderReceiver1;
    private UDPSenderReceiver senderReceiver2;


    public Scheduler() {
        this.senderReceiver1 = new UDPSenderReceiver(23, 0);
        this.senderReceiver2 = new UDPSenderReceiver(24, 0);
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

//    /**
//     * Sets the direction lamp at the given floor for the given direction to the given state
//     *
//     * @param floorNumber The floor number where the lamp is located
//     * @param direction   The direction of the lamp
//     * @param state       The target state of the lamp. (on/off)
//     */
//    public synchronized void setFloorDirectionLamp(int floorNumber, Direction direction, boolean state) {
//        this.floorSubsystems.setDirectionLamp(floorNumber, direction, state);
//    }
//
//    /**
//     * Sets the floor lamp at the given floor for the given direction to the given state
//     *
//     * @param floorNumber The floor number where the lamp is located
//     * @param direction   The direction of the lamp
//     * @param state       The target state of the lamp. (on/off)
//     */
//    public synchronized void setFloorLamp(int floorNumber, Direction direction, boolean state) {
//        this.floorSubsystems.setFloorLamp(floorNumber, direction, state);
//    }


    @Override
    public void run() {
        while (true) {
            System.out.println("Scheduler waiting for a system request.");
            SystemRequest request = senderReceiver1.receiveSystemRequest();
            int id = request.getId();
            int senderPort = senderReceiver1.getLastSenderPort();
            if (request.getType() == ADD_NEW_REQUEST) {
                assignRequestToElevator(request.getElevatorRequest());
            } else if (request.getType() == PROCESS_COMPLETED_REQUESTS) {
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
            } else if (request.getType() == PROCESSES_REQUESTS_AT_CURRENT_FLOOR) {
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
            } else if (request.getType() == NEW_PRIMARY_REQUEST) {
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                byte[] elevatorRequest = senderReceiver2.receiveResponse();
                senderReceiver1.sendResponse(elevatorRequest, senderPort);
            } else if (request.getType() == IS_STOP_REQUIRED) {
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                byte[] isRequired = senderReceiver2.receiveResponse();
                senderReceiver1.sendResponse(isRequired, senderPort);
            } else if (request.getType() == REGISTER_ELEVATOR_CONTROLLER) {
                this.addElevator(request.getId(), senderPort);
            }

            // TODO: SET_FLOOR_LAMP
            // TODO: SET_FLOOR_DIRECTION_LAMP

        }
    }

    private void assignRequestToElevator(ElevatorRequest elevatorRequest) {
        // TODO: ASSIGN THE TASK TO BEST ELEVATOR
        int elevatorId = 0;
        senderReceiver2.sendSystemRequest(new SystemRequest(ADD_NEW_REQUEST, elevatorRequest, 0), elevatorPorts[elevatorId]);
    }
}
