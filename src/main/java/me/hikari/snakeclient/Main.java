package me.hikari.snakeclient;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

public class Main
{
    public static void main( String[] args ) throws IOException {
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        TextGraphics tg = screen.newTextGraphics();
        screen.startScreen();

        tg.drawRectangle(new TerminalPosition(0,0), new TerminalSize(40,40), 'x');
        tg.putString(10,10, screen.getTerminalSize().getRows() + " " + screen.getTerminalSize().getColumns());
        screen.refresh();
        screen.readInput();
        screen.stopScreen();
    }
}
