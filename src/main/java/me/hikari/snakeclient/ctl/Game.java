package me.hikari.snakeclient.ctl;

import me.hikari.snakeclient.data.config.GameConfig;
import me.hikari.snakeclient.tui.PluggableUI;

import java.io.IOException;

public class Game {
    private GameManager manager;

    public Game(PluggableUI ui, GameConfig config) throws IOException {
        this.manager = new GameManager(ui, config);
    }

    public void start(){
        manager.start();
    }
}
