package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextImage;
import me.hikari.snakeclient.data.EngineDTO;

interface DTO2Image {
    // TODO::Tui
    TextImage dto2image(EngineDTO dto, TerminalSize viewSize, Brush brush);
}
