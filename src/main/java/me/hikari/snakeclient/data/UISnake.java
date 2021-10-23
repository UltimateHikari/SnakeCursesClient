package me.hikari.snakeclient.data;

import java.util.function.Consumer;

public interface UISnake {
    void showYourself(Consumer<Coord> placer, Coord world);
}
