package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.Symbols;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import me.hikari.snakeclient.data.Coord;
import me.hikari.snakeclient.data.GameEntry;
import me.hikari.snakeclient.data.UIConfig;
import me.hikari.snakeclient.data.UIGameEntry;

public class TuiUtils {
    private static final Integer BORDER_DELTA = -2;
    private static final Integer ENTRY_SHIFT = 1;

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
                Math.min(size.getColumns(), world.getX()),
                Math.min(size.getRows(), world.getY())
        );
    }

    public static TerminalSize removeBorder(TerminalSize size) {
        return size.withRelative(BORDER_DELTA, BORDER_DELTA);
    }

    public static TerminalSize addBorder(TerminalSize size) {
        return size.withRelative(Math.abs(BORDER_DELTA), Math.abs(BORDER_DELTA));
    }

    public static TerminalPosition shift(TerminalPosition pos, Integer row){
        return pos.withRelative(ENTRY_SHIFT, ENTRY_SHIFT + row);
    }

    public static String entryDims(UIGameEntry e){
        return  entryDims(e.getConfig());
    }
    private static String entryDims(UIConfig e){
        return  e.getWorldSize().getX() +
                "x" + e.getWorldSize().getY();
    }

    public static void putFullGameEntry(TextGraphics tg, TerminalPosition pos, UIGameEntry e) {
        tg.putString(pos.withRelative(0, 0), getName(e));
        tg.putString(pos.withRelative(0, 1), getIP(e));
        putFullConfig(tg, pos.withRelative(0,2), e.getConfig());
    }

    private static String getName(UIGameEntry e) {
        return "Name: " + e.getPlayer().getName();
    }

    private static String getIP(UIGameEntry e) {
        return "IP: " + e.getPlayer().getIp();
    }

    private static String getDims(UIConfig e) {
        return "Dims: " + entryDims(e);
    }

    private static String getFood(UIConfig e) {
        return "Food: " +
                e.getFoodStatic() +
                " + " +
                e.getFoodPerPlayer() + "p";
    }

    private static String getDelay(UIConfig e) {
        return "State delay: " + e.getStateDelayMs();
    }

    private static String getDeadProb(UIConfig e) {
        return "Dead prob: " + e.getDeadFoodProb();
    }

    public static void putFullConfig(TextGraphics tg, TerminalPosition pos, UIConfig e) {
        tg.putString(pos.withRelative(0, 0), getDims(e));
        tg.putString(pos.withRelative(0, 1), getFood(e));
        tg.putString(pos.withRelative(0, 2), getDelay(e));
        tg.putString(pos.withRelative(0, 3), getDeadProb(e));
    }
}
