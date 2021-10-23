package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.MetaEngineDTO;
import me.hikari.snakeclient.data.UIGameEntry;

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
    private UIGameEntry lastSelectedEntry = null;

    private void actualizeLastSelected(UIGameEntry selectedDTO) {
        if (!selectedDTO.equals(lastSelectedEntry)) {
            lastSelectedEntry = selectedDTO;
            screen.clear();
        }
    }

    public void show(MetaEngineDTO dto) throws IOException {
        actualizeLastSelected(dto.getSelectedEntry());
        size = TuiUtils.refreshDims(screen);
        TextGraphics tg = screen.newTextGraphics();
        drawHeader(tg);
        drawJoin(tg, dto);
        drawConfig(tg);
        screen.refresh();
    }

    private String getConfigName(UIGameEntry c) {
        return c.getPlayer().getName() +
                " " + TuiUtils.entryDims(c) +
                ": " + c.getConfig().getFoodStatic();
    }

    private void putTextWithCursor(TextGraphics tg, TerminalPosition pos, int configRowShift, UIGameEntry e, String prefix) {
        if (e == lastSelectedEntry) {
            tg.putString(
                    pos.withRelative(CONFIG_ENTRY_COL_SHIFT, CONFIG_ENTRY_ROW_SHIFT + configRowShift),
                    prefix + getConfigName(e), SGR.BLINK
            );
        } else {
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
        putTextWithCursor(tg, pos, configRowShift, dto.getDefaultEntry(), NEW_GAME);
        for (UIGameEntry e : dto.getConfigs()) {
            configRowShift++;
            putTextWithCursor(tg, pos, configRowShift, e, "");
        }
    }

    private void drawConfig(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(size.getColumns() - CONFIG_COLS, HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(CONFIG_COLS, size.getRows() - HEADER_ROWS));
        tg.putString(pos, "Game config");
        TerminalPosition text = pos.withRelative(CONFIG_ENTRY_COL_SHIFT, CONFIG_ENTRY_ROW_SHIFT);
        putFullSelectedEntry(tg, text);
    }

    private void putFullSelectedEntry(TextGraphics tg, TerminalPosition pos) {
        tg.putString(pos.withRelative(0, 0), getName());
        tg.putString(pos.withRelative(0, 1), getIP());
        tg.putString(pos.withRelative(0, 2), getDims());
        tg.putString(pos.withRelative(0, 3), getFood());
        tg.putString(pos.withRelative(0, 4), getDelay());
        tg.putString(pos.withRelative(0, 5), getDeadProb());
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

    private String getName() {
        return "Name: " + lastSelectedEntry.getPlayer().getName();
    }

    private String getIP() {
        return "Name: " + lastSelectedEntry.getPlayer().getIp();
    }

    private String getDims() {
        return "Dims: " + TuiUtils.entryDims(lastSelectedEntry);
    }

    private String getFood() {
        return "Food: " +
                lastSelectedEntry.getConfig().getFoodStatic() +
                " + " +
                lastSelectedEntry.getConfig().getFoodPerPlayer() + "p";
    }

    private String getDelay() {
        return "State delay: " + lastSelectedEntry.getConfig().getStateDelayMs();
    }

    private String getDeadProb() {
        return "Dead prob: " + lastSelectedEntry.getConfig().getDeadFoodProb();
    }

}
