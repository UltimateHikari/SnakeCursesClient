package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
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
        //todo replace with list copy, error-prone
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

    public void showYourself(Consumer<Coord> placer){
        var iter = points.iterator();
        var pos = iter.next();
        while (iter.hasNext()) {
            placer.accept(pos);
            pos = pos.withRelative(iter.next());
        }
        //safe to say, there is no snakes w/size = 1
        placer.accept(pos);
    }

}
