package me.hikari.snakeclient.ctl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.hikari.snakeclient.data.Player;

import java.io.IOException;

@RequiredArgsConstructor
public class AnnounceWorker implements Runnable {
    private final GameManager manager;
    private final Player localPlayer;
    private final CommWorker communicator;

    @Override
    public void run() {
        if (!manager.getSynchronizer().isScreenMain()) {
            if (localPlayer.isMaster()) {
                try {
                    communicator.spam(manager.getEngineDTO().retrieveAnnouncement());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
