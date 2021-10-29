package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakes.SnakesProto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@AllArgsConstructor
public class Snake implements UISnake {
    @Getter
    private Integer playerID;
    private SnakesProto.GameState.Snake.SnakeState state;
    private Coord headDirection;
    private LinkedList<Coord> points; // same logic as in protobuf for compat

    public Snake(Integer playerID, Coord direction, Coord head, Coord tailShift) {
        this(
                playerID,
                SnakesProto.GameState.Snake.SnakeState.ALIVE,
                direction,
                new LinkedList<>(Arrays.asList(head, tailShift))
        );
    }

    public Snake(Snake s) {
        this(s.playerID, s.state, s.headDirection, (LinkedList<Coord>) s.points.clone());
    }

    public Snake(SnakesProto.GameState.Snake snake) {
        this(
                snake.getPlayerId(),
                snake.getState(),
                new Coord(snake.getHeadDirection()),
                (LinkedList<Coord>) snake.getPointsList()
                        .stream().map(Coord::new).collect(Collectors.toUnmodifiableList())
        );
    }

    public Coord moveHead(Coord world) {
        var newHead = points.get(0).withRelative(headDirection, world);
        points.set(0, headDirection.withReverse());
        points.addFirst(newHead);
        return newHead;
    }

    public void dropTail() {
        points.remove(points.size() - 1);
    }

    public void turnHead(Coord direction) {
        if (!direction.withReverse().equals(headDirection)) {
            //preventing neck-eating
            headDirection = direction;
        }
    }

    public void die() {
        //TODO::Engine
        return;
    }

    public void showYourself(Consumer<Coord> placer, Coord world) {
        if (isDead()) {
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

    public boolean isDead() {
        //TODO::Engine
        return true;
    }

}
