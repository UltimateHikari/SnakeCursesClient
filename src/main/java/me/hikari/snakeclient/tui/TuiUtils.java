package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import me.hikari.snakeclient.data.Coord;

public class TuiUtils {
    private static final Integer BORDER_DELTA = -2;

    public static void drawFancyBoundary(TextGraphics tg, TerminalPosition p, TerminalSize s){
        TerminalPosition upLeft = p;
        TerminalPosition downLeft = new TerminalPosition(p.getColumn(), p.getRow() + s.getRows() - 1);
        TerminalPosition upRight = new TerminalPosition(p.getColumn() + s.getColumns() - 1, p.getRow());
        TerminalPosition downRight = new TerminalPosition(p.getColumn() + s.getColumns() - 1, p.getRow() + s.getRows() - 1);

        tg.drawLine(upLeft, downLeft, Symbols.DOUBLE_LINE_VERTICAL);
        tg.drawLine(upRight, downRight, Symbols.DOUBLE_LINE_VERTICAL);
        tg.drawLine(upLeft, upRight, Symbols.DOUBLE_LINE_HORIZONTAL);
        tg.drawLine(downLeft, downRight, Symbols.DOUBLE_LINE_HORIZONTAL);

        tg.setCharacter(upLeft, Symbols.DOUBLE_LINE_TOP_LEFT_CORNER);
        tg.setCharacter(downLeft, Symbols.DOUBLE_LINE_BOTTOM_LEFT_CORNER);
        tg.setCharacter(upRight, Symbols.DOUBLE_LINE_TOP_RIGHT_CORNER);
        tg.setCharacter(downRight, Symbols.DOUBLE_LINE_BOTTOM_RIGHT_CORNER);
    }

    public static TerminalSize refreshDims(Screen screen){
        TerminalSize newSize = screen.doResizeIfNecessary();
        if(newSize != null){
            screen.clear();
            return newSize;
        }else{
            return screen.getTerminalSize();
        }
    }

    public static TerminalSize tryShrinkSize(TerminalSize size, Coord world) {
        return new TerminalSize(
                Math.min(size.getColumns(), world.getY()),
                Math.min(size.getRows(), world.getX())
        );
    }

    public static TerminalSize removeBorder(TerminalSize size) {
        return size.withRelative(BORDER_DELTA, BORDER_DELTA);
    }

    public static TerminalSize addBorder(TerminalSize size) {
        return size.withRelative(Math.abs(BORDER_DELTA), Math.abs(BORDER_DELTA));
    }
}
