package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class MainScreen {
    private static final Integer HEADER_ROWS = 3;
    private static final Integer CREATE_COLS = 20;
    private static final Integer HEADER_TEXT_ROW = 1;

    private final Screen screen;
    //private final EventContainer;
    private TerminalSize size;

    public void show() throws IOException {
        size = TuiUtils.refreshDims(screen);
        TextGraphics tg = screen.newTextGraphics();
        drawHeader(tg);
        drawCreate(tg);
        drawJoin(tg);
        screen.refresh();
    }

    private void drawJoin(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(CREATE_COLS,HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns() - CREATE_COLS, size.getRows() - HEADER_ROWS));
        tg.putString(pos, "Join Game");
    }

    private void drawCreate(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(0,HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(CREATE_COLS, size.getRows() - HEADER_ROWS));
        tg.putString(pos, "Create Game");
    }

    private void drawHeader(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(0,0);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns(), HEADER_ROWS));
        String data = "SnakeCursesClient";
        tg.putString(size.getColumns()/2 - data.length()/2, HEADER_TEXT_ROW,  data);
        tg.putString(pos, "Header");
    }
}
