package me.hikari.snakeclient.ctl;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class UIWorker implements Runnable {
    private final GameManager manager;

    @Override
    public void run() {
        try {
            if (manager.getSynchronizer().isScreenMain()) {
                manager.getUi().showMainScreen(manager.getMetaDTO());
            } else {
                manager.getUi().showGameScreen(manager.getEngineDTO());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
