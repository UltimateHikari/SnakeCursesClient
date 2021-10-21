package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;
import me.hikari.snakeclient.data.Engine;

@AllArgsConstructor
public class EngineWorker implements Runnable {
    private Engine engine;

    @Override
    public void run() {
        engine.replenishFood();
        engine.applyMoves();
        engine.moveSnakes();
    }
}
