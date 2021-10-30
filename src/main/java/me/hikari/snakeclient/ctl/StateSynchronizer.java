package me.hikari.snakeclient.ctl;

import lombok.Getter;
import lombok.Setter;
import me.hikari.snakes.SnakesProto;

enum ActiveScreen {
    MAIN,
    GAME;
}

public class StateSynchronizer {
    private ActiveScreen screen = ActiveScreen.MAIN;
    @Getter @Setter
    public SnakesProto.NodeRole role = SnakesProto.NodeRole.VIEWER;

    boolean isScreenMain() {
        return screen == ActiveScreen.MAIN;
    }

    synchronized void switchActiveScreen() {
        if (isScreenMain()) {
            screen = ActiveScreen.GAME;
        } else {
            screen = ActiveScreen.MAIN;
        }
    }
}
