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
    public ElevatorDisplayView(ElevatorDisplayController controller) {
        //Set the Controller
        this.controller = controller;

        //Instance the JFrame
        this.setSize(720, 480);
        this.setLayout(new BorderLayout());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);

        //Create Center UI Area
        this.center = new JPanel();
        this.center.setLayout(new BoxLayout(this.center, BoxLayout.LINE_AXIS));


    }

    //View Stuff
    public JTextArea elevatorInfoPanel(Elevator elevator) {
        JTextArea
    }

    public void addElevator(Elevator elevator) {
        //Add Elevator to List of Elevators
        this.elevators.add(elevator);
        //Create a new Elevator Info Panel
        this.center.add(elevatorInfoPanel(elevator));
    }



}
