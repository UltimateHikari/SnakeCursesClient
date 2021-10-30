package me.hikari.snakeclient.ctl;

import lombok.Getter;
import me.hikari.snakeclient.data.*;
import me.hikari.snakeclient.data.config.GameConfig;
import me.hikari.snakeclient.data.config.KeyConfig;
import me.hikari.snakeclient.tui.PluggableUI;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private Player localPlayer;
    private MetaEngine gameList;
    @Getter
    private final PluggableUI ui;
    @Getter
    private StateSynchronizer synchronizer = new StateSynchronizer();
    private final CommWorker communicator;
    private final ListenWorker listener;

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
        handlers.add(scheduler.scheduleWithFixedDelay(
                new ActualizeWorker(this),
                0,
                MetaEngine.GAME_KEEP_ALIVE_MS,
                TimeUnit.MILLISECONDS));
        handlers.add(scheduler.scheduleWithFixedDelay(
                new ResendWorker(communicator),
                0,
                CommWorker.RESEND_TIMEOUT_MS,
                TimeUnit.MILLISECONDS));
        handlers.add(scheduler.schedule(
                listener,
                0,
                TimeUnit.MILLISECONDS
        ));
    }

    public GameManager(PluggableUI ui, GameConfig config) throws IOException {
        this.ui = ui;
        this.config = config;
        this.localPlayer = new Player(config.getPlayerConfig());
        this.gameList = new MetaEngine(new GameEntry(localPlayer, config.getEngineConfig()));
        this.listener = new ListenWorker(this, config.getNetConfig());
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
        listener.close();
        communicator.close();
        scheduler.shutdown();
        ui.close();
    }

    void startGame() throws IOException {
        var entry = gameList.getSelectedEntry();
        localPlayer.reset();
        currentEngine = new Engine(entry, localPlayer, communicator);
        if (entry.getJoinAddress() != null) {
            // async wait for join
            // (communicator will call join() on ack)
            localPlayer.setRole(SnakesProto.NodeRole.NORMAL);
            var joinMsg = SnakesProto.GameMessage.JoinMsg.newBuilder()
                    .setName(localPlayer.getName())
                    .build();
            var msg = SnakesProto.GameMessage.newBuilder()
                    .setJoin(joinMsg)
                    .setMsgSeq(1)
                    .build();
            communicator.sendMessage(msg, entry.getJoinAddress());
        } else {
            // local game, local as only player is already master
            spinEngine(entry.getConfig().getStateDelayMs());
            spinAnnouncer();
        }

    }

    public void join(Integer receiverID) {
        //TODO handle guys input
        System.err.println("joined");
        localPlayer.become(receiverID);
    }

    private void spinAnnouncer() {
        handlers.add(scheduler.scheduleAtFixedRate(
                new AnnounceWorker(this, localPlayer, communicator),
                0,
                1,
                TimeUnit.SECONDS));
    }

    private void spinEngine(Integer stateDelay) {
        handlers.add(scheduler.scheduleAtFixedRate(
                new EngineWorker(currentEngine),
                0,
                stateDelay,
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
        }
        throw new IllegalStateException("No game is active");
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

    void noteAnnouncement(SnakesProto.GameMessage.AnnouncementMsg msg, InetSocketAddress address) {
        gameList.addGame(new GameEntry(msg, address));
    }

    public KeyConfig getKeyconfig() {
        return config.getKeyConfig();
    }

    public void doSteer(SnakesProto.Direction direction, Peer peer) {
        currentEngine.notePeerMove(peer, direction);
    }

    public void actualizeGameList() {
        gameList.actualizeGames();
    }

    public void applyState(SnakesProto.GameState state) {
        currentEngine.applyState(state);
    }

    public Integer joinPlayer(Peer peer, String name) {
        return currentEngine.joinPlayer(peer, name);
    }

    public Integer getLocalID() {
        return localPlayer.getId();
    }
}
