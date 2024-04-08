package Display;

//Imports
import Common.Constants;
import Common.ElevatorRequest;
import Common.FaultType;
import Elevator.Elevator;
import Elevator.ElevatorSubsystem;
import Floor.Floor;
import Floor.FloorSubsystem;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

public class DisplayView extends JFrame implements Runnable{
    //View Component for Elevator Display
    private static final int REFRESH_DELAY = 100;
    private ArrayList<Elevator> elevators = new ArrayList<Elevator>();

    private JLabel[][] elevatorLabels;
    private JPanel[] elevatorPanels;
    //JPanels
    private JPanel center;
    private JScrollPane sp;
    public DisplayView(ArrayList<Elevator> elevatorList) {
        super("Elevator UI");
        this.elevators = elevatorList;
        this.elevatorLabels = new JLabel[elevatorList.size()][4];
        this.elevatorPanels = new JPanel[elevatorList.size()];

        //Frame settings
        this.setSize(720,550);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create Center Panel for Elevator Info
        this.center = new JPanel();
        this.center.setLayout(new GridLayout(1, 0));
        this.center.setPreferredSize(new Dimension(720, 480));

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
    public void displayElevators() {
        for (int i = 0; i < this.elevators.size(); i++) {
            Elevator elevator = this.elevators.get(i);

            // Create the Elev Panel
            JPanel elevPanel = new JPanel();
            elevPanel.setSize(new Dimension(320, 480)); // Increased width
            elevPanel.setLayout(new GridLayout(1,1));

            //Create Elev Info Panel
            JPanel elevInfo = new JPanel();
            elevInfo.setPreferredSize(new Dimension(320, 120));
            elevInfo.setLayout(new GridLayout(4, 1));

            //Create Labels for Elev Info
            JLabel idLabel = new JLabel("Elevator ID: " + elevator.getElevatorId());
            JLabel floorLabel = new JLabel("Floor: " + elevator.getFloorNumber());
            JLabel directionLabel = new JLabel("Direction: " + elevator.getDirection());
            JLabel doorLabel = new JLabel("Door Open: " + elevator.isDoorOpen());

            elevatorPanels[i] = elevPanel;
            elevatorLabels[i][0] = idLabel;
            elevatorLabels[i][1] = floorLabel;
            elevatorLabels[i][2] = directionLabel;
            elevatorLabels[i][3] = doorLabel;

            // Add Labels to ElevInfo
            elevInfo.add(idLabel);
            elevInfo.add(floorLabel);
            elevInfo.add(directionLabel);
            elevInfo.add(doorLabel);

            //Add Border
            elevInfo.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            //Add ElevInfo and FloorInfo to ElevPanel
            elevPanel.add(elevInfo);

            //ToDo Floor Info

            // Formatting for the Panel
            elevPanel.setBorder(BorderFactory.createLineBorder(Color.black));

            //Revalidate/Repaint
            elevPanel.revalidate();
            elevPanel.repaint();


            //Add Panel to Center
            this.center.add(elevPanel);
        }

        //Update Viewport for ScrollPane
        this.sp.setViewport(this.sp.getViewport());
    }

    public void updateDisplay() {
        for (int i = 0; i < this.elevators.size(); i++) {
            Elevator elevator = this.elevators.get(i);
            JPanel elevPanel = elevatorPanels[i];

            //Create Labels for Elev Info
            JLabel idLabel = new JLabel("Elevator ID: " + elevator.getElevatorId());
            JLabel floorLabel = new JLabel("Floor: " + elevator.getFloorNumber());
            JLabel directionLabel = new JLabel("Direction: " + elevator.getDirection());
            JLabel doorLabel = new JLabel("Door Open: " + elevator.isDoorOpen());

            elevatorLabels[i][0].setText("Elevator ID: " + elevator.getElevatorId());
            elevatorLabels[i][1].setText("Floor: " + elevator.getFloorNumber());
            elevatorLabels[i][2].setText("Direction: " + elevator.getDirection());
            elevatorLabels[i][3].setText("Door Open: " + elevator.isDoorOpen());

            //Revalidate/Repaint
            elevPanel.revalidate();
            elevPanel.repaint();


            //Add Panel to Center
//            this.center.add(elevPanel);
        }

        //Update Viewport for ScrollPane
//        this.sp.setViewport(this.sp.getViewport());
    }

    public void addElevator(Elevator elevator) {
        this.elevators.add(elevator);
        this.center.revalidate();
        this.center.repaint();
    }

    private void clearPanel(JPanel panel) {
        //Clear Components from Panel
        for (Component component : panel.getComponents()) {
            panel.remove(component);
        }
        //Revalidate and Repaint
        panel.revalidate();
        panel.repaint();
    }

    @Override
    public void run() {
        //Regularly Update the View
        displayElevators();
        while(true) {
            //Update Elevator Display
            updateDisplay();

            try {
                Thread.sleep(REFRESH_DELAY);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        //Elevator and Floor Subsystem
        FloorSubsystem fsub = new FloorSubsystem("test.txt", 22);

        //Elevators for Testing
        ElevatorSubsystem esub0 = new ElevatorSubsystem(0);
        Elevator elev0 = new Elevator(esub0, 0, false);
        ElevatorSubsystem esub1 = new ElevatorSubsystem(1);
        Elevator elev1 = new Elevator(esub1, 1, false);
        ElevatorSubsystem esub2 = new ElevatorSubsystem(2);
        Elevator elev2 = new Elevator(esub2, 2, false);
        ElevatorSubsystem esub3 = new ElevatorSubsystem(3);
        Elevator elev3 = new Elevator(esub3, 3, false);
        ElevatorSubsystem esub4 = new ElevatorSubsystem(4);
        Elevator elev4 = new Elevator(esub4, 4, false);
        ElevatorSubsystem esub5 = new ElevatorSubsystem(0);
        Elevator elev5 = new Elevator(esub5, 5, false);

        //View
        DisplayView view = new DisplayView(Elevator.elevList);

        //Test Displaying Info
        view.displayElevators();
    }
}
