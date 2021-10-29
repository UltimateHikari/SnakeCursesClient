package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.BasicTextImage;
import com.googlecode.lanterna.graphics.TextImage;
import me.hikari.snakeclient.data.*;


class LeftCorneredView implements DTO2Image {
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

    private void putSnake(TextColor color, UISnake s, Coord worldSize) {
        s.showYourself(c -> placeCharacter(coord2pos(c), 'S', color), worldSize);
    }

    private TextColor getColor(EngineDTO dto, UISnake s){
        var player = dto.getPlayers()
                .stream()
                .filter(p -> p.getId().equals(s.getPlayerID()))
                .findFirst();
        if(player.isPresent()){
            return brush.getColor(player.get());
        }
        return TextColor.ANSI.WHITE; // zombie color
    }

    @Override
    public TextImage dto2image(EngineDTO dto, TerminalSize viewSize, Brush brush) {
        size = viewSize;
        image = new BasicTextImage(viewSize);
        var worldSize = dto.getConfig().getWorldSize();
        this.brush = brush;

        for (Coord f : dto.getFoods()) {
            // TODO::Tui
            placeCharacter(coord2pos(f), 'F', TextColor.ANSI.YELLOW_BRIGHT);
        }
        // It's O(n2), but fine for low amounts player/snake
        dto.getSnakes().forEach(s -> putSnake(getColor(dto,s), s, worldSize));
        return image;
    }
}
