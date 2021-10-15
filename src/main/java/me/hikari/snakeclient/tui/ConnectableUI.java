package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.input.KeyStroke;
import me.hikari.snakeclient.data.EngineGetter;
import me.hikari.snakeclient.data.MetaEngineGetter;

public interface ConnectableUI {
    void showMainScreen(MetaEngineGetter engine);
    void showGameScreen(EngineGetter engine);
    KeyStroke getInput();
}
