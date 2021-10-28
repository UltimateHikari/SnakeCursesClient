package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;
import me.hikari.snakeclient.data.Engine;

@AllArgsConstructor
public class EngineWorker implements Runnable {
    private Engine engine;

    /**
     * TODO
     * add graceful exit when snakes are broken
     */

    @Override
    public void run() {
        engine.replenishFood();
        engine.applyMoves();
        try {
            engine.moveSnakes();
        }catch (Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
