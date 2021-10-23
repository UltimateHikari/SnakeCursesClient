package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Consumer;

enum SnakeState{
    ALIVE,
    ZOMBIE,
    DEAD;
}

@AllArgsConstructor
public class Snake implements UISnake{
    // TODO mb some monitor protection? first place for searching for error
    private SnakeState state;
    private Coord headDirection;
    private LinkedList<Coord> points; // same logic as in protobuf for compat

    public Snake(Coord direction, Coord head, Coord tailShift) {
        this(SnakeState.ALIVE, direction, new LinkedList<>(Arrays.asList(head, tailShift)));
    }

    public Snake(Snake s) {
        this(s.state, s.headDirection, (LinkedList<Coord>) s.points.clone());
    }

    public Coord moveHead(Coord world) {
        if(state != SnakeState.DEAD) {
            var newHead = points.get(0).withRelative(headDirection, world);
            points.set(0, headDirection.withReverse());
            points.addFirst(newHead);
            return newHead;
        }
        //hoping for cautious use outside
        return points.get(0);
    }

    public void dropTail() {
        points.remove(points.size() - 1);
    }

    public void turnHead(Coord direction) {
        if (state == SnakeState.ALIVE && !direction.withReverse().equals(headDirection)) {
            //preventing neck-eating
            headDirection = direction;
        }
    }

    public void die(){
        state = SnakeState.DEAD;
    }

    public void zombie(){
        if(state != SnakeState.DEAD){
            state = SnakeState.ZOMBIE;
        }
    }

    public void showYourself(Consumer<Coord> placer, Coord world) {
        if(isDead()){
            return;
        }
        var iter = points.iterator();
        var pos = iter.next();
        while (iter.hasNext()) {
            placer.accept(pos);
            pos = pos.withRelative(iter.next(), world);
        }
        //safe to say, there is no snakes w/size = 1
        placer.accept(pos);
    }

    public boolean isDead(){
        return state == SnakeState.DEAD;
    }

}
