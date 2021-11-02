package me.hikari.snakeclient.ctl;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class ActualizeWorker implements Runnable{
    private final GameManager manager;


    @Override
    public void run() {
        manager.actualizeGameList();
    }
}
