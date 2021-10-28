package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

class GameGrid {
    public  final String HEADER = "SnakeCursesClient::Game";
    private  final Integer HEADER_ROWS = 3;
    private  final Integer FOOTER_ROWS = 3;
    private  final Integer INFO_COLS = 25;
    private  final Integer INFO_ROWS = 10;

     TerminalPosition getHeaderPos(TerminalSize size){
        return new TerminalPosition(0, 0);
    }
     TerminalSize getHeaderSize(TerminalSize size){
        return new TerminalSize(size.getColumns(), HEADER_ROWS);
    }

     TerminalPosition getFooterPos(TerminalSize size){
        return new TerminalPosition(0, size.getRows() - FOOTER_ROWS);
    }
     TerminalSize getFooterSize(TerminalSize size){
        return new TerminalSize(size.getColumns(), FOOTER_ROWS);
    }

     TerminalPosition getFieldPos(TerminalSize size){
        return new TerminalPosition(0, HEADER_ROWS);
    }
     TerminalSize getFieldSize(TerminalSize size){
        return new TerminalSize(
                size.getColumns() - INFO_COLS,
                size.getRows() - HEADER_ROWS - FOOTER_ROWS
        );
    }

     TerminalPosition getInfoPos(TerminalSize size){
        return new TerminalPosition(size.getColumns() - INFO_COLS, HEADER_ROWS);
    }
     TerminalSize getInfoSize(TerminalSize size){
        return new TerminalSize(INFO_COLS, INFO_ROWS);
    }

     TerminalPosition getScorePos(TerminalSize size){
        return new TerminalPosition(
                size.getColumns() - INFO_COLS,
                HEADER_ROWS + INFO_ROWS
        );
    }
     TerminalSize getScoreSize(TerminalSize size){
        return new TerminalSize(
                INFO_COLS,
                size.getRows() - HEADER_ROWS - INFO_ROWS - FOOTER_ROWS
        );
    }
}
