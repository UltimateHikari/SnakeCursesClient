package me.hikari.snakeclient.ctl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.Engine;
import me.hikari.snakeclient.data.MetaEngine;
import me.hikari.snakeclient.data.Player;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.tui.PluggableUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameManager /*implements ManagerDTO*/{
    private static final int UI_REFRESH_RATE_MS = 10;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private ScheduledFuture<?> currentGame = null;
    private List<ScheduledFuture<?>> handlers = new ArrayList<>();

    @Getter
    private Engine currentEngine = null;
    @Getter
    private MetaEngine gameList = new MetaEngine();
    @Getter
    private final PluggableUI ui;
    @Getter
    private StateSynchronizer synchronizer = new StateSynchronizer();

    private void startWorkers(){
        handlers.add(scheduler.scheduleAtFixedRate(
                new UIWorker(this),
                0,
                UI_REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS));
        handlers.add(scheduler.scheduleAtFixedRate(
                new InputWorker(this),
                0,
                UI_REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS));
    }

    public GameManager(PluggableUI ui){
        this.ui = ui;
        startWorkers();
    }

    public void startGame(EngineConfig config) {
        currentEngine = new Engine(config);
        handlers.add(scheduler.scheduleAtFixedRate(
                new EngineWorker(currentEngine),
                0,
                config.getStateDelayMs(),
                TimeUnit.MILLISECONDS));
    }

    public void stopGame() {
        currentGame.cancel(false);
        currentEngine = null;
    }

    public void close() throws IOException {
        handlers.forEach(h -> h.cancel(false));
        scheduler.shutdown();
        ui.close();
    }
}
