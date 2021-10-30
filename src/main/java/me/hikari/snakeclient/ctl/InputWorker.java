package me.hikari.snakeclient.ctl;

import com.googlecode.lanterna.input.KeyStroke;
import me.hikari.snakeclient.data.config.KeyConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;

class InputWorker implements Runnable {
    private final GameManager manager;
    private final StateSynchronizer state;
    private final KeyConfig keys;

    public InputWorker(GameManager manager){
        this.manager = manager;
        this.keys = manager.getKeyconfig();
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
        // switch cannot handle non-constant getters
        var c = stroke.getCharacter();

        if(c == keys.getQuit()){
            manager.close();
        }

        if(c == keys.getStart()){
            if(state.isScreenMain()) {
                manager.startGame();
                state.switchActiveScreen();
            }
        }

        if(c == keys.getStop()){
            if(!state.isScreenMain()) {
                manager.stopGame();
                state.switchActiveScreen();
            }
        }

        if(c == keys.getUp()){
            if(state.isScreenMain()){
                manager.navUp();
            } else {
                if(state.getRole() == SnakesProto.NodeRole.MASTER) {
                    manager.moveSnake(SnakesProto.Direction.UP);
                    return;
                }
                if(state.getRole() == SnakesProto.NodeRole.NORMAL) {
                    manager.sendSteer(SnakesProto.Direction.UP);
                }
            }
        }

        if(c == keys.getDown()){
            if(state.isScreenMain()){
                manager.navDown();
            } else {
                if(state.getRole() == SnakesProto.NodeRole.MASTER) {
                    manager.moveSnake(SnakesProto.Direction.DOWN);
                    return;
                }
                if(state.getRole() == SnakesProto.NodeRole.NORMAL) {
                    manager.sendSteer(SnakesProto.Direction.DOWN);
                }
            }
        }

        if(c == keys.getLeft()){
            if(!state.isScreenMain()){
                if(state.getRole() == SnakesProto.NodeRole.MASTER) {
                    manager.moveSnake(SnakesProto.Direction.LEFT);
                    return;
                }
                if(state.getRole() == SnakesProto.NodeRole.NORMAL) {
                    manager.sendSteer(SnakesProto.Direction.LEFT);
                }
            }
        }

        if(c == keys.getRight()){
            if(!state.isScreenMain()){
                if(state.getRole() == SnakesProto.NodeRole.MASTER) {
                    manager.moveSnake(SnakesProto.Direction.RIGHT);
                    return;
                }
                if(state.getRole() == SnakesProto.NodeRole.NORMAL) {
                    manager.sendSteer(SnakesProto.Direction.RIGHT);
                }
            }
        }

    }
}
