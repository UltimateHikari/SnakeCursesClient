package me.hikari.snakeclient.ctl;

import com.googlecode.lanterna.input.KeyStroke;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;

interface InputDelegate {
    StateSynchronizer getSynchronizer();

    KeyStroke getInput() throws IOException;

    void close() throws IOException;

    void startGame() throws IOException;

    void stopGame();

    void noteNavUp();

    void noteNavDown();

    void noteSnakeMove(SnakesProto.Direction up);

    void sendSteer(SnakesProto.Direction up) throws IOException;

}
