package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Snake {
    private boolean isZombie;
    private Direction headDirection;
    private List<Coord> points; // same logic as in protobuf for compat

    public Snake(Direction direction, Coord head, Coord tailShift) {
        this(false, direction, Arrays.asList(head, tailShift));
    }

    public Snake(Snake s){
        this(s.isZombie, s.headDirection, s.points.stream().collect(Collectors.toUnmodifiableList()));
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
