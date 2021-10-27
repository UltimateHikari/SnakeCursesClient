package me.hikari.snakeclient.ctl;

import lombok.Getter;
import me.hikari.snakeclient.data.*;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.data.config.GameConfig;
import me.hikari.snakeclient.data.config.KeyConfig;
import me.hikari.snakeclient.tui.PluggableUI;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameManager {
    private static final int UI_REFRESH_RATE_MS = 10;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private ScheduledFuture<?> currentGame = null;
    private List<ScheduledFuture<?>> handlers = new ArrayList<>();

    private final GameConfig config;
    private Engine currentEngine = null;
    private MetaEngine gameList;
    @Getter
    private final PluggableUI ui;
    @Getter
    private StateSynchronizer synchronizer = new StateSynchronizer();
    private final CommWorker communicator;

    private void startWorkers() throws IOException {
        handlers.add(scheduler.scheduleAtFixedRate(
                new UIWorker(this),
                0,
                UI_REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS));
        handlers.add(scheduler.scheduleWithFixedDelay(
                new InputWorker(this),
                0,
                UI_REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS));
        handlers.add(scheduler.schedule(
                new ListenWorker(this, config.getNetConfig()),
                0,
                TimeUnit.MILLISECONDS
        ));
    }

    public GameManager(PluggableUI ui, GameConfig config) throws IOException {
        // TODO encase network stuff into NetConfig or sth
        this.ui = ui;
        this.config = config;
        this.gameList = new MetaEngine(new GameEntry(new Player(config.getPlayerConfig()), config.getEngineConfig()));
        this.communicator = new CommWorker(this, config.getNetConfig(), config.getPlayerConfig().getPort());
        startWorkers();
    }

    public void start() {
        /**
         * endless loop on listening inside communicator;
         * our boy also can send stuff, but via handles for other workers
         */
        communicator.run();
    }

    public void close() throws IOException {
        handlers.forEach(h -> h.cancel(true));
        communicator.close();
        scheduler.shutdown();
        ui.close();
    }

    void startGame() {
        var entry = gameList.getSelectedEntry();
        currentEngine = new Engine(entry);

        handlers.add(scheduler.scheduleAtFixedRate(
                new EngineWorker(currentEngine),
                0,
                entry.getConfig().getStateDelayMs(),
                TimeUnit.MILLISECONDS));
    }

    void stopGame() {
        currentGame.cancel(true);
        currentEngine = null;
    }

    MetaEngineDTO getMetaDTO() {
        return gameList.getDTO();
    }

    EngineDTO getEngineDTO() {
        if (currentEngine != null) {
            return currentEngine.getDTO();
        } else {
            // TODO mb throw exception on access error?
            return null;
        }
    }

    void navDown() {
        gameList.navDown();
    }

    void navUp() {
        gameList.navUp();
    }

    void moveSnake(SnakesProto.Direction dir) {
        currentEngine.noteHostMove(dir);
    }

    void noteAnnouncement(SnakesProto.GameMessage.AnnouncementMsg msg, InetAddress address) {
        gameList.addGame(new GameEntry(msg, address));
    }

    public KeyConfig getKeyconfig() {
        return config.getKeyConfig();
    }

    public void doSteer(SnakesProto.Direction direction, Peer peer) {

    }
}
