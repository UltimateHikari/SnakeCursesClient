package me.hikari.snakeclient.ctl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.hikari.snakeclient.data.Player;

@RequiredArgsConstructor
public class AnnounceWorker implements Runnable {
    private final GameManager manager;
    private final Player localPlayer;
    private final CommWorker communicator;

    @SneakyThrows
    @Override
    public void run() {
        if (!manager.getSynchronizer().isScreenMain()) {
            if (localPlayer.isMaster()) {
                communicator.spam(manager.getEngineDTO().retrieveAnnouncement());
            }
        }
    }
}
