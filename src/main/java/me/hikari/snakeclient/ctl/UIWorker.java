package me.hikari.snakeclient.ctl;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;

@RequiredArgsConstructor
class UIWorker implements Runnable {
    private final GameManager manager;

    @SneakyThrows
    @Override
    public void run() {
        if (manager.getSynchronizer().isScreenMain()) {
            manager.getUi().showMainScreen(manager.getMetaDTO());
        } else {
            manager.getUi().showGameScreen(manager.getEngineDTO());
        }
    }
}
