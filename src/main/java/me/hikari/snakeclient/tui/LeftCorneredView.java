package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import me.hikari.snakeclient.data.Coord;
import me.hikari.snakeclient.data.EngineDTO;
import me.hikari.snakeclient.data.Player;
import me.hikari.snakeclient.data.UISnake;

public class LeftCorneredView implements DTO2Image {
    private TerminalSize size;
    private TextImage image;
    private Brush brush;

    private TerminalPosition coord2pos(Coord c) {
        return new TerminalPosition(c.getX(), c.getY());
    }

    private void placeCharacter(TerminalPosition p, Character c, TextColor color) {
        if (p.getColumn() < size.getColumns() && p.getRow() < size.getRows()) {
            image.setCharacterAt(p, TextCharacter.fromCharacter(c)[0].withForegroundColor(color));
        }
    }

    private void putSnake(Player p, UISnake s, Coord worldSize) {
        s.showYourself(c -> placeCharacter(coord2pos(c), 'S', brush.getColor(p)), worldSize);
    }

    @Override
    public TextImage dto2image(EngineDTO dto, TerminalSize viewSize, Brush brush) {
        size = viewSize;
        image = new BasicTextImage(viewSize);
        var worldSize = dto.getUiConfig().getWorldSize();
        this.brush = brush;

        for (Coord f : dto.getFoods()) {
            //TODO-1 move char to config
            placeCharacter(coord2pos(f), 'F', TextColor.ANSI.YELLOW_BRIGHT);
        }
        dto.getSnakeMap().forEach((p, s) -> putSnake(p, s, worldSize));
        return image;
    }
}
