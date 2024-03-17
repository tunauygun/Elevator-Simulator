package Floor;

import Common.*;

import static Common.SystemRequestType.*;

/**
 * FloorController.java
 * <p>
 * Represents the floor controller responsible for managing floor related requests from the scheduler.
 *
 * @version 1.0, March 17, 2024
 */
public class FloorController implements Runnable {

    private FloorSubsystem floorSubsystem;
    private UDPSenderReceiver receiver;

    /**
     * Constructs a new FloorController for the specified floor subsystem.
     *
     * @param floorSubsystem The floor subsystem associated with this controller.
     */
    public FloorController(FloorSubsystem floorSubsystem) {
        this.floorSubsystem = floorSubsystem;
        this.receiver = new UDPSenderReceiver(Constants.FLOOR_CONTROLLER_PORT, 0);
    }

    /**
     * Continuously receives and processes system requests from the elevator scheduler.
     */
    @Override
    public void run() {
        while (true) {
            SystemRequest request = receiver.receiveSystemRequest();
            if (request.getType() == SET_FLOOR_LAMPS) {
                this.floorSubsystem.setFloorLamp(request.getFloorNumber(), request.getDirection(), request.getState());
            } else if (request.getType() == SET_FLOOR_DIRECTION_LAMPS) {
                this.floorSubsystem.setDirectionLamp(request.getId(), request.getFloorNumber(), request.getDirection(), request.getState());
            }
        }
    }
}
