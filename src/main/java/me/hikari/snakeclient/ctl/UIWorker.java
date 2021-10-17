package me.hikari.snakeclient.ctl;


import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.tui.PluggableUI;

import java.io.IOException;

@RequiredArgsConstructor
public class UIWorker implements Runnable {
    private final GameManager manager;

    @Override
    public void run() {
        try {
            if (manager.getSynchronizer().isScreenMain()) {
                manager.getUi().showMainScreen(manager.getMetaDTO(), manager.getSynchronizer().popNavDirection());
            } else {
                manager.getUi().showGameScreen(manager.getEngineDTO());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
