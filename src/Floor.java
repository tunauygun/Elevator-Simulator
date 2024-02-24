import java.time.LocalTime;
import java.util.ArrayList;

public class Floor {
    private int floorNumber;
    private ArrayList<ElevatorRequest> downRequests;
    private ArrayList<ElevatorRequest> upRequests;

    private boolean directionLampUp;
    private boolean directionLampDown;
    private boolean floorLampUp;
    private boolean floorLampDown;
    private boolean isFirstFloor;
    private boolean isLastFloor;


    public Floor(int floorNumber, int numberOfFloors) {
        this.floorNumber = floorNumber;
        this.downRequests = new ArrayList<>();
        this.upRequests = new ArrayList<>();
        this.directionLampUp = false;
        this.directionLampDown = false;
        this.floorLampUp = false;
        this.floorLampDown = false;
        this.isFirstFloor = floorNumber == 1;
        this.isLastFloor = floorNumber == numberOfFloors;
    }

    public void addRequest(ElevatorRequest event) {
        switch (event.getDirection()) {
            case UP -> upRequests.add(event);
            case DOWN -> downRequests.add(event);
        }
    }

    public ElevatorRequest checkForRequests() {
        for (int i = 0; i < upRequests.size(); i++) {
            if (upRequests.get(i).getTime().isBefore(LocalTime.now())) {
                this.floorLampUp = true;
                return upRequests.remove(i);
            }
        }
        for (int i = 0; i < downRequests.size(); i++) {
            if (downRequests.get(i).getTime().isBefore(LocalTime.now())) {
                this.floorLampDown = true;
                return downRequests.remove(i);
            }
        }
        return null;
    }

    public void setDirectionLamp(Direction direction, boolean state) {
        if (direction == Direction.UP && !isLastFloor) {
            directionLampUp = state;
        } else if (direction == Direction.DOWN && !isFirstFloor) {
            directionLampDown = state;
        }
    }

    public void setFloorLamp(Direction direction, boolean state) {
        if (direction == Direction.UP && !isLastFloor) {
            floorLampUp = state;
        } else if (direction == Direction.DOWN && !isFirstFloor) {
            floorLampDown = state;
        }
    }
}
