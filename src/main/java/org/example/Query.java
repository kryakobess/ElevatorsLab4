package org.example;

import lombok.Data;

import static org.example.Direction.DOWN;
import static org.example.Direction.UP;

@Data
public class Query {
    private int from;
    private int to;
    Direction direction;

    public Query(int from, int to) {
        this.from = from;
        this.to = to;
        direction = from < to ? UP : DOWN;
    }
}
