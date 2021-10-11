package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;

public class TuiUtils {
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
}
