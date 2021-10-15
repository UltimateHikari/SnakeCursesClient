package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import me.hikari.snakeclient.data.Engine;
import me.hikari.snakeclient.data.EngineGetter;
import me.hikari.snakeclient.data.MetaEngineGetter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * screens:
 * new game //TODO
 * enter existing //TODO
 * gamescreen
 */

public class Tui implements PluggableUI {
    private static final long REFRESH_RATE_MS = 10;
    private static final long NO_DELAY = 0;
    private Screen screen;

    private final GameScreen gameScreen;
    private final MainScreen mainScreen;

    public Tui() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        gameScreen = new GameScreen(screen);
        mainScreen = new MainScreen(screen);
        screen.startScreen();
    }

    public void close() throws IOException {
        screen.stopScreen();
    }

    @Override
    public void showMainScreen(MetaEngineGetter engine) throws IOException {
        mainScreen.show();
    }

    @Override
    public void showGameScreen(EngineGetter engine) throws IOException {
        gameScreen.show();
    }

    @Override
    public KeyStroke getInput() throws IOException {
        return screen.readInput();
    }
}
