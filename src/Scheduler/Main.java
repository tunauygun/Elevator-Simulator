package Scheduler;

public class Main {
    public static void main(String[] args) {

        Scheduler sch = new Scheduler();

        Thread scheduler = new Thread(sch, "Scheduler");

        scheduler.start();
    }
}
