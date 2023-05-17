package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args){
        Scanner in = new Scanner(System.in);
        ElevatorFactory elevatorFactory = new ElevatorFactory(20, 1000);
        elevatorFactory.runElevators();
        while (true) {
            Query q = new Query(in.nextInt(), in.nextInt());
            elevatorFactory.sendQuery(q);
        }
    }
}
