package me.hikari.snakeclient;

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

        tg.putString(10,10, "Hello world1");
        screen.refresh();
        screen.readInput();
        screen.stopScreen();
        //System.out.println( "Hello World+1!" );
    }
}
