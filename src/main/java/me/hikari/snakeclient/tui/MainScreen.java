package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.ctl.NavDirection;
import me.hikari.snakeclient.data.MetaEngineDTO;
import me.hikari.snakeclient.data.config.EngineConfig;

import java.io.IOException;

@RequiredArgsConstructor
public class MainScreen {
    private static final Integer HEADER_ROWS = 3;
    private static final Integer CONFIG_COLS = 40;
    private static final Integer CONFIG_ENTRY_COL_SHIFT = 1;
    private static final Integer CONFIG_ENTRY_ROW_SHIFT = 1;
    private static final Integer HEADER_TEXT_ROW = 1;
    private static final String NEW_GAME = "New game: ";

    private final Screen screen;
    //private final EventContainer;
    private TerminalSize size;
    private int listCursorPosition = 0;
    private EngineConfig chosenConfig;

    private void moveListCirsor(int listSize, NavDirection direction) {
        switch (direction) {
            case UP:
                listCursorPosition--;
                break;
            case DOWN:
                listCursorPosition++;
                break;
            default:
                break;
        }
        if (listCursorPosition < 0) {
            listCursorPosition = 0;
        }
        if (listCursorPosition >= listSize) {
            listCursorPosition %= listSize;
        }
    }

    public void show(MetaEngineDTO dto, NavDirection navDirection) throws IOException {
        moveListCirsor(dto.getConfigs().size() + 1, navDirection);
        size = TuiUtils.refreshDims(screen);
        TextGraphics tg = screen.newTextGraphics();
        drawHeader(tg);
        drawJoin(tg, dto);
        drawConfig(tg);
        screen.refresh();
    }

    private String getConfigName(EngineConfig config) {
        //TODO host & player name
        return config.getWidth() + "x" + config.getHeight() + ": " + config.getFoodStatic();
    }

    private void putTextWithCursor(TextGraphics tg, TerminalPosition pos, int configRowShift, EngineConfig e, String prefix){
        if (listCursorPosition == configRowShift) {
            chosenConfig = e;
            tg.putString(
                    pos.withRelative(CONFIG_ENTRY_COL_SHIFT, CONFIG_ENTRY_ROW_SHIFT + configRowShift),
                    prefix + getConfigName(e), SGR.BLINK
            );
        }else {
            tg.putString(
                    pos.withRelative(CONFIG_ENTRY_COL_SHIFT, CONFIG_ENTRY_ROW_SHIFT + configRowShift),
                    prefix + getConfigName(e)
            );
        }
    }

    private void drawJoin(TextGraphics tg, MetaEngineDTO dto) {
        TerminalPosition pos = new TerminalPosition(0, HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns() - CONFIG_COLS, size.getRows() - HEADER_ROWS));
        tg.putString(pos, "Join Game");
        int configRowShift = 0;
        putTextWithCursor(tg, pos, configRowShift, dto.getDefaultConfig(), NEW_GAME);
        for (EngineConfig e : dto.getConfigs()) {
            configRowShift++;
            putTextWithCursor(tg, pos, configRowShift, e, "");
        }
    }

    private void drawConfig(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(CONFIG_COLS, HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(CONFIG_COLS, size.getRows() - HEADER_ROWS));
        tg.putString(pos, "Game config");
        tg.putString(pos.withRelative(CONFIG_ENTRY_COL_SHIFT, CONFIG_ENTRY_ROW_SHIFT), getFullConfig(chosenConfig));
    }

    private String getFullConfig(EngineConfig chosenConfig) {
        //TODO verbosity
        return getConfigName(chosenConfig);
    }

    private void drawHeader(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(0, 0);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns(), HEADER_ROWS));
        String data = "SnakeCursesClient";
        tg.putString(size.getColumns() / 2 - data.length() / 2, HEADER_TEXT_ROW, data);
        tg.putString(pos, "Header");
    }
}
