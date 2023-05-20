package org.example;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ElevatorFactory elevatorFactory = new ElevatorFactory(20, 1000);
        elevatorFactory.runElevators();

        QueryFactory queryFactory = new QueryFactory(elevatorFactory, 3000);
        queryFactory.startSendingQueries();

        TimeUnit.SECONDS.sleep(5);

        queryFactory.stopSendingQueries();
        elevatorFactory.stopElevators();
    }
}
