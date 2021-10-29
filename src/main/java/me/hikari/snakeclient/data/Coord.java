package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.hikari.snakes.SnakesProto;

@AllArgsConstructor
@EqualsAndHashCode
public class Coord {
    @Getter
    private final int x;
    @Getter
    private final int y;

    public Coord(SnakesProto.Direction d) {
        switch (d) {
            case UP -> {
                x = 0;
                y = -1;
            }
            case DOWN -> {
                x = 0;
                y = 1;
            }
            case LEFT -> {
                x = -1;
                y = 0;
            }
            case RIGHT -> {
                x = 1;
                y = 0;
            }
            default -> {
                x = 0;
                y = 0;
            }
        }
    }

    public Coord(SnakesProto.GameState.Coord c) {
        this(c.getX(), c.getY());
    }

    public Coord withRelative(Coord c, Coord world) {
        return new Coord((x + c.x + world.x) % world.x, (y + c.y + world.y) % world.y);
    }

    public Coord withReverse(){
        return new Coord(x*-1, y*-1);
    }

    @Override
    public String toString(){
        return "(" + x + ", " + y + ")";
    }

    public SnakesProto.GameState.Coord retrieve() {
        return SnakesProto.GameState.Coord.newBuilder().setX(x).setY(y).build();
    }

    public SnakesProto.Direction direction() {
        if(x == 0 && y == -1){
            return SnakesProto.Direction.UP;
        }
        if(x == 0 && y == 1){
            return SnakesProto.Direction.DOWN;
        }
        if(x == -1 && y == 0){
            return SnakesProto.Direction.LEFT;
        }
        return SnakesProto.Direction.RIGHT;
    }
}
