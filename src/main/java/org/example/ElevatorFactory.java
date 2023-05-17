package org.example;

public class ElevatorFactory {

    private final Elevator e1;
    private final Elevator e2;

    private final Thread e1Thread;
    private final Thread e2Thread;

    public ElevatorFactory(int floorsCount, int elevatorMovingDelayInMillis) {
        e1 = new Elevator(1, floorsCount, elevatorMovingDelayInMillis);
        e2 = new Elevator(2, floorsCount, elevatorMovingDelayInMillis);
        e1Thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    e1.start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
        e2Thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    e2.start();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public void sendQuery(Query query) {
        int e1HandleResponse = e1.canHandle(query);
        int e2HandleResponse = e2.canHandle(query);
        if (e1HandleResponse == -2 || e2HandleResponse == -2) {
            System.out.println("There is no such floors");
            return;
        }
        if (e1HandleResponse == e2HandleResponse) {
            if (e1.getCurrentCapacity() < e2.getCurrentCapacity()) {
                e1.invoke(query);
            } else {
                e2.invoke(query);
            }
        }
        else if (e1HandleResponse < e2HandleResponse) {
            e1.invoke(query);
        }
        else {
            e2.invoke(query);
        }
    }

    public void runElevators() {
        e1Thread.start();
        e2Thread.start();
    }

}
