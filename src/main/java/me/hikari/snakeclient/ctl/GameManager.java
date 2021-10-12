package me.hikari.snakeclient.ctl;

import me.hikari.snakeclient.data.Engine;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.tui.ConnectableUI;
import me.hikari.snakeclient.tui.Tui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameManager {
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentGame = null;
    private Engine engine = null;
    private List<ConnectableUI> subs = new ArrayList<>();

    public void startGame(EngineConfig config) {
        engine = new Engine(config);
        scheduler.scheduleAtFixedRate(
                new EngineWorker(engine),
                0,
                config.getStateDelayMs(),
                TimeUnit.MILLISECONDS);
    }

    public void stopGame() {
        currentGame.cancel(true);
        engine = null;
    }

    public void close() {
        scheduler.shutdown();
        for (ConnectableUI ui: subs) {
            ui.engineUnsubscribe();
        }
    }

    public void connectUI(ConnectableUI ui) {
        ui.engineSubscribe(engine);
        subs.add(ui);
    }
}
