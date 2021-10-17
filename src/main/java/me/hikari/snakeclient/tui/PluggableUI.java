package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.input.KeyStroke;
import me.hikari.snakeclient.ctl.NavDirection;
import me.hikari.snakeclient.data.EngineDTO;
import me.hikari.snakeclient.data.MetaEngineDTO;

import java.io.IOException;

public interface PluggableUI {
    void showMainScreen(MetaEngineDTO engine, NavDirection navDirection) throws IOException;
    void showGameScreen(EngineDTO engine) throws IOException;
    KeyStroke getInput() throws IOException;
    void close() throws IOException;
}
