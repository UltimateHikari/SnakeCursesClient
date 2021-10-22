package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Coord {
    @Getter
    private final int x;
    @Getter
    private final int y;

    public Coord(Direction d) {
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

    public Coord withRelative(Coord c) {
        return new Coord(x + c.x, y + c.y);
    }
    public Coord withRelative(Coord c, Coord world) {
        return new Coord((x + c.x + world.x) % world.x, (y + c.y + world.y) % world.y);
    }

    public Coord withReverse(){
        return new Coord(x*-1, y*-1);
    }
}
