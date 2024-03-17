package Scheduler;

import Common.*;
import Floor.FloorSubsystem;

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

    private int elevatorTurn = 0;


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
                System.out.println("Received new request from floor " + request.getFloorNumber());
                assignRequestToElevator(request.getElevatorRequest());
            } else if (request.getType() == PROCESS_COMPLETED_REQUESTS) {
                LogPrinter.print(request.getId(), "Received new PROCESS_COMPLETED_REQUESTS request from Elevator " + request.getId());
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
            } else if (request.getType() == PROCESSES_REQUESTS_AT_CURRENT_FLOOR) {
                LogPrinter.print(request.getId(),"Received new PROCESSES_REQUESTS_AT_CURRENT_FLOOR request from Elevator " + request.getId());
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
            } else if (request.getType() == NEW_PRIMARY_REQUEST) {
                LogPrinter.print(request.getId(),"Elevator " + request.getId() + " is asking for a new primary request");
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                byte[] elevatorRequest = senderReceiver2.receiveResponse();
                senderReceiver1.sendResponse(elevatorRequest, senderPort);
                LogPrinter.print(request.getId(),"Sending Elevator " + request.getId() + " its new primary request");
            } else if (request.getType() == IS_STOP_REQUIRED) {
                LogPrinter.print(request.getId(),"Elevator " + request.getId() + " asking if a stop is required at floor " + request.getFloorNumber());
                senderReceiver2.sendSystemRequest(request, elevatorPorts[id]);
                byte[] isRequired = senderReceiver2.receiveResponse();
                senderReceiver1.sendResponse(isRequired, senderPort);
                LogPrinter.print(request.getId(),"Replying to Elevator " + request.getId() + "'s IS_STOP_REQUIRED request");
            } else if (request.getType() == REGISTER_ELEVATOR_CONTROLLER) {
                this.addElevator(request.getId(), senderPort);
                LogPrinter.print(request.getId(),"Registering Elevator " + request.getId() + " at port " + senderPort);
            } else if (request.getType() == SET_FLOOR_LAMPS) {
                LogPrinter.print(request.getId(),"Received SET_FLOOR_LAMPS request. Forwarding it to Floor Controller");
                senderReceiver2.sendSystemRequest(request, Constants.FLOOR_CONTROLLER_PORT);
            } else if (request.getType() == SET_FLOOR_DIRECTION_LAMPS) {
                LogPrinter.print(request.getId(),"Received SET_FLOOR_DIRECTION_LAMPS request. Forwarding it to Floor Controller");
                senderReceiver2.sendSystemRequest(request, Constants.FLOOR_CONTROLLER_PORT);
            }
        }
    }

    private void assignRequestToElevator(ElevatorRequest elevatorRequest) {
        // TODO: ASSIGN THE TASK TO BEST ELEVATOR

        senderReceiver2.sendSystemRequest(new SystemRequest(ADD_NEW_REQUEST, elevatorRequest, 0), elevatorPorts[elevatorTurn]);
        System.out.println("Assigning the request to Elevator " + elevatorTurn);
        this.elevatorTurn = (elevatorTurn + 1) % elevatorPorts.length;

    }
}
