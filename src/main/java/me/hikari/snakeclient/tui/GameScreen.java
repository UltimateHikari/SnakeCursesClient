package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.EngineDTO;
import me.hikari.snakeclient.data.Player;
import me.hikari.snakeclient.data.UIConfig;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
class GameScreen {
    private static final TerminalPosition IMAGE_SHIFT = new TerminalPosition(1, 1);

    private final Screen screen;
    private final DTO2Image converter;
    private final String footer;
    private final GameGrid grid = new GameGrid();

    private TerminalSize size;
    private final Brush brush = new Brush();

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

    private void drawField(TextGraphics tg, EngineDTO dto) {
        var pos = grid.getFieldPos(size);
        var border = grid.getFieldSize(size);
        TuiUtils.drawFancyBoundary(tg, pos, border);
        var viewSize = TuiUtils.tryShrinkSize(TuiUtils.removeBorder(border), dto.getConfig().getWorldSize());
        TuiUtils.drawFancyBoundary(tg, pos, TuiUtils.addBorder(viewSize));
        tg.putString(pos, "Field");

        tg.drawImage(pos.withRelative(IMAGE_SHIFT), converter.dto2image(dto, viewSize, brush));
    }

    private void drawInfo(TextGraphics tg, UIConfig config) {
        var pos = grid.getInfoPos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getInfoSize(size));
        tg.putString(pos, "Info");
        TuiUtils.putFullConfig(tg, TuiUtils.shift(pos, 0), config);
    }

    private void drawScores(TextGraphics tg, List<Player> players) {
        var pos = grid.getScorePos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getScoreSize(size));
        tg.putString(pos, "Highscores");
        TuiUtils.clearRectangleByBorder(tg, pos, grid.getScoreSize(size));
        Player[] sPlayers = players.stream()
                .sorted(Comparator.comparing(Player::getScore))
                .toArray(Player[]::new);
        for (int i = 0; i < sPlayers.length; i++) {
            tg.setForegroundColor(brush.getColor(sPlayers[i]));
            tg.putString(TuiUtils.shift(pos, i), TuiUtils.playerScore(sPlayers[i]));
        }
        tg.clearModifiers();
    }

    public void show(EngineDTO dto) throws IOException {
        size = TuiUtils.refreshDims(screen);
        TextGraphics tg = screen.newTextGraphics();
        drawHeader(tg);
        drawFooter(tg, dto.getError());
        drawField(tg, dto);
        drawInfo(tg, dto.getConfig());
        drawScores(tg, dto.getPlayers());
        screen.refresh();
    }

    private void drawFooter(TextGraphics tg, String error) {
        var pos = grid.getFooterPos(size);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                grid.getFooterSize(size));
        if (error != null) {
            tg.putString(TuiUtils.center(pos, size, error.length()), error);
        } else {
            tg.putString(TuiUtils.center(pos, size, footer.length()), footer);
        }
    }
}
