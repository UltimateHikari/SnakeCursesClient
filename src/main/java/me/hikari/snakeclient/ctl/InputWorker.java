package me.hikari.snakeclient.ctl;

import com.googlecode.lanterna.input.KeyStroke;

import java.io.IOException;

public class InputWorker implements Runnable {
    //TODO get back to synchronizer & supplier<KeyStroke>
    private final GameManager manager;
    private final StateSynchronizer state;

    public InputWorker(GameManager manager){
        this.manager = manager;
        state = manager.getSynchronizer();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                KeyStroke stroke = manager.getUi().getInput();
                tryHandleStroke(stroke);
                //System.err.println(stroke.getCharacter() + " " + stroke.getEventTime());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tryHandleStroke(KeyStroke stroke) throws IOException {
        switch (stroke.getCharacter()) {
            //TODO move magic keys to config class
            //TODO h for help on keys?
            case 'j':
                manager.navDown();
                break;
            case 'k':
                manager.navUp();
                break;
            case 'g':
                if(state.isScreenMain()) {
                    manager.startGame();
                    state.switchActiveScreen();
                }
                break;
            case 'q':
                manager.close();
                break;
            case 'e':
                state.switchActiveScreen();
                break;
            default:
                System.err.println(stroke.getCharacter());
        }
    }
}
