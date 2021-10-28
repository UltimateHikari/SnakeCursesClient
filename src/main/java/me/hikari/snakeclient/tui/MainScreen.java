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
class MainScreen {
    private final Screen screen;
    private final String footer;
    private final MainGrid grid = new MainGrid();

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
        drawFooter(tg);
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
                    TuiUtils.shift(pos, configRowShift),
                    prefix + getConfigName(e), SGR.BLINK
            );
        } else {
            tg.putString(
                    TuiUtils.shift(pos, configRowShift),
                    prefix + getConfigName(e)
            );
        }
    }

    private void drawJoin(TextGraphics tg, MetaEngineDTO dto) {
        var pos = grid.getJoinPos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getJoinSize(size));
        tg.putString(pos, "Join Game");
        int configRowShift = 0;
        putTextWithCursor(tg, pos, configRowShift, dto.getDefaultEntry(), grid.NEW_GAME);
        for (UIGameEntry e : dto.getConfigs()) {
            configRowShift++;
            putTextWithCursor(tg, pos, configRowShift, e, "");
        }
    }

    private void drawConfig(TextGraphics tg) {
        var pos = grid.getConfigPos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getConfigSize(size));
        tg.putString(pos, "Game config");
        TuiUtils.putFullGameEntry(tg, TuiUtils.shift(pos, 0), lastSelectedEntry);
    }

    private void drawHeader(TextGraphics tg) {
        var pos = grid.getHeaderPos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getHeaderSize(size));
        String data = grid.HEADER;
        tg.putString(TuiUtils.center(pos, size, data.length()), data);
        tg.putString(pos, "Header");
    }

    private void drawFooter(TextGraphics tg) {
        var pos = grid.getFooterPos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getFooterSize(size));
        tg.putString(TuiUtils.center(pos, size, footer.length()), footer);
    }
}
