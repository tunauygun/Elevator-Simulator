package Floor;

import Common.*;

import static Common.SystemRequestType.*;

public class FloorController implements Runnable {

    private FloorSubsystem floorSubsystem;
    private UDPSenderReceiver receiver;

    public FloorController(FloorSubsystem floorSubsystem) {
        this.floorSubsystem = floorSubsystem;
        this.receiver = new UDPSenderReceiver(Constants.FLOOR_CONTROLLER_PORT, 0);
    }

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
