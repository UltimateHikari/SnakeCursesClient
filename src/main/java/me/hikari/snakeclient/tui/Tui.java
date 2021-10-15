package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.Engine;

import javax.swing.*;
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

public class Tui implements ConnectableUI {
    private static final long REFRESH_RATE_MS = 10;
    private static final long NO_DELAY = 0;
    private Engine engine = null;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> currentScreenTask = null;
    private Screen screen;

    public Tui() throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
    }

    public void showGameScreen() {
        cancelCurrentScreen();
        currentScreenTask = scheduler.scheduleAtFixedRate(
                new GameScreen(screen),
                NO_DELAY,
                Tui.REFRESH_RATE_MS,
                TimeUnit.MILLISECONDS
        );
    }

    public void showMainScreen(){

    }

    public void close() throws IOException {
        cancelCurrentScreen();
        screen.stopScreen();
        scheduler.shutdown();
    }

    private void cancelCurrentScreen() {
        screen.clear();
        if (currentScreenTask != null) {
            System.out.println("cancelling...");
            System.out.println(currentScreenTask.cancel(true));
        }
    }
}
