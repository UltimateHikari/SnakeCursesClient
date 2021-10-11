package me.hikari.snakeclient;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import me.hikari.snakeclient.tui.Tui;
import me.hikari.snakeclient.tui.TuiUtils;

import java.io.IOException;

public class Main
{
    public static void main( String[] args ) throws IOException, InterruptedException {
        Tui tui = new Tui();
        tui.showGameScreen();
        Thread.sleep(10000);
        tui.close();
        System.out.println("screen cancelled");
    }
}
