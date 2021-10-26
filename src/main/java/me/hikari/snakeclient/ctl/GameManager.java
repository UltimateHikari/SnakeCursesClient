package me.hikari.snakeclient.ctl;

import lombok.Getter;
import me.hikari.snakeclient.data.*;
import me.hikari.snakeclient.data.EngineConfig;
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

public class GameManager{
    private static final int UI_REFRESH_RATE_MS = 10;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    private ScheduledFuture<?> currentGame = null;
    private List<ScheduledFuture<?>> handlers = new ArrayList<>();

    private Engine currentEngine = null;
    private MetaEngine gameList = new MetaEngine();
    @Getter
    private final PluggableUI ui;
    @Getter
    private StateSynchronizer synchronizer = new StateSynchronizer();
    @Getter
    private KeyConfig keyconfig;

    private final InetSocketAddress group;
    private final Integer selfPort;

    private void startWorkers(){
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
    }

    public GameManager(PluggableUI ui, KeyConfig keyconfig, String addr, Integer port, Integer selfPort) throws UnknownHostException {
        // TODO encase network stuff into NetConfig or sth
        this.ui = ui;
        this.keyconfig = keyconfig;
        this.group = new InetSocketAddress(InetAddress.getByName(addr), port);
        this.selfPort = selfPort;
        startWorkers();
        //TODO-0 remove test
        gameList.addGame(new Player("dummy", 1, "255.255.255.255"), new EngineConfig());
    }

    public void start(){
        /**
         * TODO main loop of sending/recving messages
         */
    }
    public void close() throws IOException {
        handlers.forEach(h -> h.cancel(true));
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

    MetaEngineDTO getMetaDTO(){
        return gameList.getDTO();
    }

    EngineDTO getEngineDTO(){
        if(currentEngine != null){
            return currentEngine.getDTO();
        }else{
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

    void moveSnake(Direction dir){
        currentEngine.noteHostMove(dir);
    }

    void noteAnnouncement(SnakesProto.GameMessage.AnnouncementMsg msg){
        // TODO do stuff and then
        //gameList.addGame(player, config);
    }
}
