package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.input.KeyStroke;
import me.hikari.snakeclient.data.EngineGetter;
import me.hikari.snakeclient.data.MetaEngineGetter;

import java.io.IOException;

public interface PluggableUI {
    void showMainScreen(MetaEngineGetter engine) throws IOException;
    void showGameScreen(EngineGetter engine) throws IOException;
    KeyStroke getInput() throws IOException;
    void close() throws IOException;
}
