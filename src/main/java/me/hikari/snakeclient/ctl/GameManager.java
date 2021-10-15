package me.hikari.snakeclient.ctl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.Engine;
import me.hikari.snakeclient.data.MetaEngine;
import me.hikari.snakeclient.data.Player;
import me.hikari.snakeclient.tui.PluggableUI;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameManager /*implements ManagerDTO*/{
    private static final int UI_REFRESH_RATE_MS = 10;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private ScheduledFuture<?> currentGame = null;

    @Getter
    private Engine currentEngine = null;
    @Getter
    private MetaEngine gameList = new MetaEngine();
    @Getter
    private final PluggableUI ui;
    @Getter
    private StateSynchronizer synchronizer = new StateSynchronizer();

    private void startWorkers(){
        //TODO save handlers
        scheduler.scheduleAtFixedRate(
                new UIWorker(this),
                0,
                UI_REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(
                new InputWorker(this),
                0,
                UI_REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS);
    }

    public GameManager(PluggableUI ui){
        this.ui = ui;
        startWorkers();
    }

//    public void startGame(EngineConfig config) {
//        engine = new Engine(config);
//        scheduler.scheduleAtFixedRate(
//                new EngineWorker(engine),
//                0,
//                config.getStateDelayMs(),
//                TimeUnit.MILLISECONDS);
//    }

    public void stopGame() {
        currentGame.cancel(true);
        currentEngine = null;
    }

    public void close() {
        //TODO close handlers
        scheduler.shutdown();
    }
}
