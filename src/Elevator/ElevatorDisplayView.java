package Elevator;

//Imports
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ElevatorDisplayView extends JFrame{
    //View Component for Elevator Display
    private final ElevatorDisplayController controller;
    private ArrayList<Elevator> elevators = new ArrayList<Elevator>();
    //JPanels
    private JPanel center;
    private JScrollPane sp;
    public ElevatorDisplayView(ElevatorDisplayController controller) {
        super("Elevator UI");
        this.controller = controller;

        //Frame settings
        this.setSize(720,480);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create Center Panel for Elevator Info
        this.center = new JPanel();
        this.center.setLayout(new BoxLayout(this.center, BoxLayout.LINE_AXIS));
        this.center.setPreferredSize(new Dimension(720, 240));

        //Create Scroll Pane for Center Panel
        this.sp = new JScrollPane(this.center);
        this.sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        //Add Scroll Pane to Frame
        this.add(sp, BorderLayout.CENTER);

        //Set The Frame to be Visible
        this.setVisible(true);
    }

    //View Stuff
    public JPanel elevatorInfoPanel(Elevator elevator) {
        //Create the Elev Panel
        JPanel elevPanel = new JPanel();
        elevPanel.setMinimumSize(new Dimension(320, 240));
        elevPanel.setLayout(new GridLayout(4,1));

        // Create JTextArea to hold elevator information
        JTextArea elevInfo = new JTextArea();
        elevInfo.setEditable(false); // Set to non-editable
        elevInfo.append("Elevator ID: " + elevator.getElevatorId() + "\n");
        elevInfo.append("Floor: " + elevator.getFloorNumber() + "\n");
        elevInfo.append("Direction: " + elevator.getDirection() + "\n");
        elevInfo.append("Door Open: " + elevator.isDoorOpen() + "\n");

        // Add JTextArea to Panel
        elevPanel.add(elevInfo, BorderLayout.CENTER);

        //Formatting for the Panel
        elevPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        return elevPanel;
    }

    public void addElevator(Elevator elevator) {
        //Add Elevator to List of Elevators
        this.elevators.add(elevator);
        //Create a new Elevator Info Panel
        this.center.add(elevatorInfoPanel(elevator));
    }


    public static void main(String[] args) {
        ElevatorDisplayController controller = new ElevatorDisplayController();
        ElevatorSubsystem sub = new ElevatorSubsystem(0);
        Elevator elev0 = new Elevator(sub, 0, false);
        Elevator elev1 = new Elevator(sub, 1, false);
        Elevator elev2 = new Elevator(sub, 2, false);
        Elevator elev3 = new Elevator(sub, 3, false);
        Elevator elev4 = new Elevator(sub, 4, false);
        Elevator elev5 = new Elevator(sub, 5, false);


        ElevatorDisplayView view = new ElevatorDisplayView(controller);
        view.addElevator(elev0);
        view.addElevator(elev1);
        view.addElevator(elev2);
        view.addElevator(elev3);
        view.addElevator(elev4);
        view.addElevator(elev5);
    }
}
