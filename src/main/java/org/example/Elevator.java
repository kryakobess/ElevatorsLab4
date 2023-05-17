package org.example;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.abs;
import static org.example.Direction.*;

@Data
public class Elevator {

    public final int floorsCount;
    public final int moveDelayInMillis;
    private final ArrayList<ArrayList<Query>> queryWaitingList = new ArrayList<>();
    private final Set<Integer> queryDestinations = new HashSet<>();
    private int destinationFloor;
    private int elevatorId;
    private int currentFloor;
    private Direction direction;

    public Elevator(int elevatorId, int floorsCount, int moveDelayInMillis){
        this.elevatorId = elevatorId;
        this.currentFloor = 1;
        this.direction = WAITING;
        this.floorsCount = floorsCount;
        this.moveDelayInMillis = moveDelayInMillis;
        for (int i = 0; i <= floorsCount; ++i){
            queryWaitingList.add(new ArrayList<>());
        }
    }

    public void invoke(Query query) {
        queryWaitingList.get(query.getFrom()).add(query);
    }

    public int getCurrentCapacity() {
        return (int) queryWaitingList.stream().filter(lq -> !lq.isEmpty()).count();
    }

    public int canHandle(Query q) {
        if (q.getFrom() > floorsCount || q.getTo() > floorsCount ||
                q.getFrom() <= 0 || q.getTo() <= 0) {
            return -2;
        }
        int diff = abs(q.getFrom() - currentFloor);
        return switch (direction) {
            case UP ->  q.getFrom() > currentFloor && q.getDirection().equals(UP) ? diff : -1;
            case DOWN -> q.getFrom() < currentFloor && q.getDirection().equals(DOWN) ? diff : -1;
            case WAITING -> diff;
        };
    }

    public void start() throws InterruptedException {
        if (hasQueries()) {
            System.out.printf("Elevator №%d is searching for relevant queries\n", getElevatorId());
            findInitialQuery();
            do {
                checkFloor();
                if (direction.equals(WAITING)) {
                    findDestination();
                }
            } while (move());
        }
    }

    private boolean hasQueries(){
        return !queryWaitingList.stream().filter(lq -> !lq.isEmpty()).toList().isEmpty();
    }

    private void checkFloor() throws InterruptedException {
        if (floorHasQuery() || queryDestinations.contains(currentFloor)){
            System.out.printf("Elevator №%d has stopped\n", elevatorId);
            Thread.sleep(moveDelayInMillis);
            removeLeftQueries();
            acceptNewQueriesFromCurrentFloor();
            if (!queryDestinations.isEmpty()) {
                destinationFloor = Collections.max(queryDestinations);
            }
        }
    }

    private void findDestination() {
        if (!queryDestinations.isEmpty()) {
            switch (direction) {
                case WAITING -> {
                    int minDiff = Integer.MAX_VALUE;
                    for (var d : queryDestinations) {
                        if (abs(d - currentFloor) < minDiff) {
                            minDiff = abs(d - currentFloor);
                            destinationFloor = d;
                        }
                    }
                    direction = currentFloor < destinationFloor ? UP : DOWN;
                }
                case UP -> destinationFloor = Collections.max(queryDestinations);
                case DOWN -> destinationFloor = Collections.min(queryDestinations);
            }
        }
    }

    private void findInitialQuery(){
        if (direction.equals(WAITING)) {
            int optimalFloor = 0;
            int minDiff = Integer.MAX_VALUE;
            for (int i = 1; i <= floorsCount; ++i){
                if (!queryWaitingList.get(i).isEmpty()){
                    if (abs(i - currentFloor) < minDiff) {
                        minDiff = abs(i - currentFloor);
                        optimalFloor = i;
                    }
                }
            }
            destinationFloor = optimalFloor;
            direction = currentFloor < destinationFloor ? UP : DOWN;
            if (currentFloor == destinationFloor) direction = WAITING;
        }
    }

    private void removeLeftQueries() {
        queryDestinations.remove(currentFloor);
    }

    private boolean move() throws InterruptedException {
        System.out.printf("Elevator №%d on the %d floor\n", elevatorId, currentFloor);
        Thread.sleep(moveDelayInMillis);
        switch (direction) {
            case UP -> currentFloor++;
            case DOWN -> currentFloor--;
            case WAITING -> {
                return false;
            }
        }
        if (currentFloor == destinationFloor) direction = WAITING;
        return true;
    }


    private boolean floorHasQuery(){
        return !queryWaitingList.get(currentFloor).isEmpty();
    }

    private void acceptNewQueriesFromCurrentFloor(){
        for (var query : queryWaitingList.get(currentFloor)) {
            queryDestinations.add(query.getTo());
        }
        queryWaitingList.get(currentFloor).clear();
        findDestination();
    }
}