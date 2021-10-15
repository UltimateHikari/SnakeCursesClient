package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class Snake {
    private boolean isZombie;
    private Direction headDirection;
    private List<Coord> points; // same logic as in protobuf for compat

    public Snake(Direction direction, Coord head, Coord tailShift) {
        this(false, direction, Arrays.asList(head, tailShift));
    }

    public Coord moveHead(){
        return null;
    }

    public Coord dropTail(){
        return null;
    }

    public void turnHead(Direction direction){
        headDirection = direction;
    }

    public void showYourself(FieldRepresentation field){

    }
}
