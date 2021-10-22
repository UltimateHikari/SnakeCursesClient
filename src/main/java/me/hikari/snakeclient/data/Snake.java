package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Consumer;

@AllArgsConstructor
public class Snake {
    //TODO implement unguided snake interface with only show yourself for ui
    // TODO mb some monitor protection? first place for searching for error
    private boolean isZombie;
    private Coord headDirection;
    private LinkedList<Coord> points; // same logic as in protobuf for compat

    public Snake(Coord direction, Coord head, Coord tailShift) {
        this(false, direction, new LinkedList<>(Arrays.asList(head, tailShift)));
    }

    public Snake(Snake s) {
        this(s.isZombie, s.headDirection, (LinkedList<Coord>) s.points.clone());
    }

    public Coord moveHead(Coord world) {
        var newHead = points.get(0).withRelative(headDirection, world);
        points.set(0, headDirection.withReverse());
        points.addFirst(newHead);
        return newHead;
    }

    public Coord dropTail() {
        return points.remove(points.size() - 1);
    }

    public void turnHead(Coord direction) {
        if (!(direction == points.get(1))) {
            //preventing neck-eating
            headDirection = direction;
        }
    }

    public void showYourself(Consumer<Coord> placer, Coord world) {
        var iter = points.iterator();
        var pos = iter.next();
        while (iter.hasNext()) {
            placer.accept(pos);
            pos = pos.withRelative(iter.next(), world);
        }
        //safe to say, there is no snakes w/size = 1
        placer.accept(pos);
    }

}
