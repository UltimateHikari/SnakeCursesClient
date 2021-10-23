package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class GameScreen {
    private static final Integer HEADER_ROWS = 3;
    private static final Integer INFO_COLS = 25;
    private static final Integer INFO_ROWS = 10;
    private static final TerminalPosition IMAGE_SHIFT = new TerminalPosition(1, 1);
    private static final Integer HEADER_TEXT_ROW = 1;

    private final Screen screen;
    private TerminalSize size;
    private final DTO2Image converter;
    private Brush brush = new Brush();

    private void drawHeader(TextGraphics tg) {
        TerminalPosition pos = new TerminalPosition(0, 0);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(size.getColumns(), HEADER_ROWS));
        String data = String.valueOf(System.currentTimeMillis());
        tg.putString(size.getColumns() / 2 - data.length() / 2, HEADER_TEXT_ROW, data);
        tg.putString(pos, "Header");
    }

    private void drawField(TextGraphics tg, EngineDTO dto) {
        var pos = new TerminalPosition(0, HEADER_ROWS);
        var border = new TerminalSize(
                size.getColumns() - INFO_COLS,
                size.getRows() - HEADER_ROWS);
        TuiUtils.drawFancyBoundary(tg, pos, border);
        var viewSize = TuiUtils.tryShrinkSize(TuiUtils.removeBorder(border), dto.getUiConfig().getWorldSize());
        TuiUtils.drawFancyBoundary(tg, pos, TuiUtils.addBorder(viewSize));
        tg.putString(pos, "Field");

        tg.drawImage(pos.withRelative(IMAGE_SHIFT), converter.dto2image(dto, viewSize, brush));
    }

    private void drawInfo(TextGraphics tg, UIConfig config) {
        TerminalPosition pos = new TerminalPosition(size.getColumns() - INFO_COLS, HEADER_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(INFO_COLS, INFO_ROWS));
        tg.putString(pos, "Info");
        TuiUtils.putFullConfig(tg, TuiUtils.shift(pos, 0), config);
    }

    private void drawScores(TextGraphics tg, Set<Player> players) {
        TerminalPosition pos = new TerminalPosition(size.getColumns() - INFO_COLS, HEADER_ROWS + INFO_ROWS);
        TuiUtils.drawFancyBoundary(
                tg,
                pos,
                new TerminalSize(INFO_COLS, size.getRows() - HEADER_ROWS - INFO_ROWS));
        tg.putString(pos, "Highscores");
        try {
            Player [] sPlayers = players.stream()
                    .sorted(Comparator.comparing(Player::getScore))
                    .toArray(size -> new Player[size]);
            for (int i = 0; i < sPlayers.length; i++) {
                tg.setForegroundColor(brush.getColor(sPlayers[i]));
                tg.putString(TuiUtils.shift(pos, i), playerScore(sPlayers[i]));
            }
            tg.clearModifiers();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private String playerScore(Player p) {
        return p.getName() + ": " + p.getScore();
    }

    public void show(EngineDTO dto) throws IOException {
        //TODO make footer with controls, like in htop; same for mainscreen
        size = TuiUtils.refreshDims(screen);
        TextGraphics tg = screen.newTextGraphics();
        drawHeader(tg);
        drawField(tg, dto);
        drawInfo(tg, dto.getUiConfig());
        drawScores(tg, dto.getSnakeMap().keySet());
        screen.refresh();
    }
}
