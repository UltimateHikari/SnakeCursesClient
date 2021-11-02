package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;
import me.hikari.snakeclient.data.Engine;

import java.io.IOException;

/**
 * Worker for MASTER and DEPUTY nodes
 * starts when host player is MASTER
 * or elected by MASTER as DEPUTY
 */

@AllArgsConstructor
class EngineWorker implements Runnable {
    private Engine engine;

    @Override
    public void run() {
        try {
            engine.doStep();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
