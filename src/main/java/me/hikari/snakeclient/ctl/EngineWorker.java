package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;
import me.hikari.snakeclient.data.Engine;

/**
 * Worker for MASTER and DEPUTY nodes
 * starts when host player is MASTER
 * or elected by MASTER as DEPUTY
 */

@AllArgsConstructor
public class EngineWorker implements Runnable {
    private Engine engine;

    @Override
    public void run() {
        engine.doStep();
    }
}
