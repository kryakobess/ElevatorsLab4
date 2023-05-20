package org.example;

import java.util.concurrent.ThreadLocalRandom;

public class QueryFactory {

    private final int queryInterval;
    private final ElevatorFactory elevatorFactory;

    private final Thread queriesThread;

    public QueryFactory(ElevatorFactory elevatorFactory, int queryInterval) {
        this.elevatorFactory = elevatorFactory;
        this.queryInterval = queryInterval;
        queriesThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                int from = ThreadLocalRandom.current().nextInt(1, elevatorFactory.getFloorsCount() + 1);
                int to = ThreadLocalRandom.current().nextInt(1, elevatorFactory.getFloorsCount() + 1);
                Query q = new Query(from, to);
                System.out.printf("Query from %d with direction %s\n", q.getFrom(), q.getDirection());
                elevatorFactory.sendQuery(q);
                try {
                    Thread.sleep(queryInterval);
                } catch (Exception e) {
                    break;
                }
            }
        });
    }

    public void startSendingQueries() {
        queriesThread.start();
    }

    public void stopSendingQueries() {
        System.out.println("STOP SENDING QUERIES");
        queriesThread.interrupt();
    }
}
