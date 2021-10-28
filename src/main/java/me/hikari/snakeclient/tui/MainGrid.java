package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;

class MainGrid {
    public  final String HEADER = "SnakeCursesClient::MainMenu";
    private  final Integer HEADER_ROWS = 3;
    private  final Integer FOOTER_ROWS = 3;
    private  final Integer CONFIG_COLS = 40;
    public  final String NEW_GAME = "New game: ";

     TerminalPosition getHeaderPos(TerminalSize size) {
        return new TerminalPosition(0, 0);
    }

     TerminalSize getHeaderSize(TerminalSize size) {
        return new TerminalSize(size.getColumns(), HEADER_ROWS);
    }

     TerminalPosition getFooterPos(TerminalSize size) {
        return new TerminalPosition(0, size.getRows() - FOOTER_ROWS);
    }

     TerminalSize getFooterSize(TerminalSize size) {
        return new TerminalSize(size.getColumns(), FOOTER_ROWS);
    }

     TerminalPosition getJoinPos(TerminalSize size) {
        return new TerminalPosition(0, HEADER_ROWS);
    }

     TerminalSize getJoinSize(TerminalSize size) {
        return new TerminalSize(
                size.getColumns() - CONFIG_COLS,
                size.getRows() - HEADER_ROWS - FOOTER_ROWS
        );
    }

    TerminalPosition getConfigPos(TerminalSize size) {
        return new TerminalPosition(
                size.getColumns() - CONFIG_COLS,
                HEADER_ROWS
        );
    }

     TerminalSize getConfigSize(TerminalSize size) {
        return new TerminalSize(CONFIG_COLS, size.getRows() - HEADER_ROWS - FOOTER_ROWS);
    }

}
