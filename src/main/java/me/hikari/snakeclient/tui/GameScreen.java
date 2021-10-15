package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;

@RequiredArgsConstructor
public class GameScreen {
    private static final Integer HEADER_ROWS = 3;
    private static final Integer INFO_COLS = 20;
    private static final Integer INFO_ROWS = 10;
    private static final Integer HEADER_TEXT_ROW = 1;

    private final Screen screen;
    //private final EventContainer;
    private TerminalSize size;

    private void drawHeader(TextGraphics tg){
        TerminalPosition pos = new TerminalPosition(0,0);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns(), HEADER_ROWS));
        String data = String.valueOf(System.currentTimeMillis());
        tg.putString(size.getColumns()/2 - data.length()/2, HEADER_TEXT_ROW,  data);
        tg.putString(pos, "Header");
    }
    private void drawField(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(0,HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns() - INFO_COLS, size.getRows() - HEADER_ROWS));
        tg.putString(pos, "Field");
        //TODO check on EventContainer
    }
    private void drawInfo(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(size.getColumns() - INFO_COLS,HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(INFO_COLS, INFO_ROWS));
        tg.putString(pos,"Info");
    }
    private void drawScores(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(size.getColumns() - INFO_COLS,HEADER_ROWS + INFO_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(INFO_COLS, size.getRows() - HEADER_ROWS - INFO_ROWS));
        tg.putString(pos,"Highscores");
    }

    public void show() throws IOException {
        size = TuiUtils.refreshDims(screen);
        TextGraphics tg = screen.newTextGraphics();
        drawHeader(tg);
        drawField(tg);
        drawInfo(tg);
        drawScores(tg);
        screen.refresh();
    }
}
