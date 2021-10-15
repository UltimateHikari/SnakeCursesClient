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
            //TODO save current screen, clear if needed(or in TUI more appropriate)
            if (manager.getSynchronizer().isScreenMain()) {
                manager.getUi().showMainScreen(manager.getGameList());
            } else {
                manager.getUi().showGameScreen(manager.getCurrentEngine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
