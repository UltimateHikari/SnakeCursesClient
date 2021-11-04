package me.hikari.snakeclient.ctl;

import lombok.RequiredArgsConstructor;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;

@RequiredArgsConstructor
class AnnounceWorker implements Runnable {
    private final GameManager manager;
    private final CommWorker communicator;

    @Override
    public void run() {
        if (!manager.getSynchronizer().isScreenMain()) {
            if (manager.getSynchronizer().getRole() == SnakesProto.NodeRole.MASTER) {
                try {
                    communicator.spam(manager.getEngineDTO().retrieveAnnouncement());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
