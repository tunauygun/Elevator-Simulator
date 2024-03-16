package Elevator;

import Common.*;

import java.util.Iterator;

import static Common.SystemRequestType.*;

public class ElevatorController implements Runnable{

    private UDPSenderReceiver senderReceiver;
    private Elevator elevator;
    private ElevatorSubsystem subsystem;

    public ElevatorController(Elevator elevator, ElevatorSubsystem subsystem) {
        this.elevator = elevator;
        this.subsystem = subsystem;

        this.senderReceiver = new UDPSenderReceiver(0, Constants.SCHEDULER_PORT_2);

        this.senderReceiver.sendSystemRequest(new SystemRequest(REGISTER_ELEVATOR_CONTROLLER, elevator.getElevatorId()), Constants.SCHEDULER_PORT);
    }

    /**
     * Sends the given elevator request to an available elevator
     * @param request The new elevator request
     */
    public synchronized void addNewRequest(ElevatorRequest request) {
        subsystem.addNewRequest(request);
        LogPrinter.print(this.elevator.getElevatorId(), "Elevator " + this.elevator.getElevatorId() + ": New Request Received: " + request);
    }


    /**
     * Check all request in the queue for the given direction. If the request is completed by stoping at the given
     * floor number, the request is completed and removed form the queue
     * @param floorNumber The floor number of the elevator stop
     * @param direction The direction of travel of the elevator
     */
    public synchronized void processCompletedRequests(int floorNumber, Direction direction) {
        subsystem.processCompletedRequests(floorNumber, direction);
    }

    /**
     * Process the request at the given floor for the given direction by picking up the
     * passengers/elevator requests and updating the elevator lamps.
     * @param floorNumber The floor number
     * @param direction The elevator direction of travel
     */
    public synchronized void processRequestsAtCurrentFloor(int floorNumber, Direction direction) {
        subsystem.processRequestsAtCurrentFloor(floorNumber, direction);
    }

    /**
     * Return a new primary request for the elevator from the request queue. This method prioritizes
     * elevator requests that are partially processed (which means that the elevator picked up the passenger
     * but still didn't stop at their destination floor). If there are no partianlly completed requests, returns
     * any available request. If the request queue is empty, returns null.
     * @return A new primary elevator request
     */
    public synchronized ElevatorRequest receiveNewPrimaryRequest() {
        return subsystem.receiveNewPrimaryRequest();
    }


    /** Checks of a stop is required at any given floor for the given direction
     * @param nextFloorNumber The floor number
     * @param direction The direction of travel
     * @return True if a stop is required, false otherwise
     */
    public synchronized boolean isStopRequiredForFloor(int nextFloorNumber, Direction direction) {
        return subsystem.isStopRequiredForFloor(nextFloorNumber, direction);
    }


    @Override
    public void run() {
        while (true) {
            SystemRequest request = senderReceiver.receiveSystemRequest();
            if(request.getType() == ADD_NEW_REQUEST){
                addNewRequest(request.getElevatorRequest());
            }else if(request.getType() == PROCESS_COMPLETED_REQUESTS){
                processCompletedRequests(request.getFloorNumber(), request.getDirection());
            }else if(request.getType() == PROCESSES_REQUESTS_AT_CURRENT_FLOOR){
                processRequestsAtCurrentFloor(request.getFloorNumber(), request.getDirection());
            }else if(request.getType() == NEW_PRIMARY_REQUEST){
                ElevatorRequest er = receiveNewPrimaryRequest();
                senderReceiver.sendResponse(ElevatorRequest.serializeRequest(er), Constants.SCHEDULER_PORT_2);
            }else if(request.getType() == IS_STOP_REQUIRED){
                boolean isRequired = isStopRequiredForFloor(request.getFloorNumber(), request.getDirection());
                byte[] data = new byte[1];
                data[0] = (byte) (isRequired? 1 : 0);
                senderReceiver.sendResponse(data, Constants.SCHEDULER_PORT_2);
            }

        }
    }
}
