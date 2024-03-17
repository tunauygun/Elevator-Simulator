package Scheduler;

/**
 * Main class for initializing and starting scheduler.
 *
 * @version 1.0, March 17, 2024
 */
public class Main {
    public static void main(String[] args) {

        Scheduler sch = new Scheduler();

        Thread scheduler = new Thread(sch, "Scheduler");

        scheduler.start();
    }
}
