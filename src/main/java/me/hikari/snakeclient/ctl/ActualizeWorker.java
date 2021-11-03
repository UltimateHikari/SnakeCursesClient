package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;

import java.io.IOException;

@AllArgsConstructor
class ActualizeWorker implements Runnable{
    private final GameManager manager;


    @Override
    public void run() {
        try {
            manager.actualizeNodeInfo();
        } catch (IOException e) {
            e.printStackTrace();
        }
        manager.actualizeGameList();
    }
}
