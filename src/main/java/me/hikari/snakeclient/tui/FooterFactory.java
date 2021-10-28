package me.hikari.snakeclient.tui;

import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.config.KeyConfig;

@RequiredArgsConstructor
class FooterFactory {
    private final KeyConfig config;

    String getMainFooter(){
        return new StringBuilder()
                .append("quit: ")
                .append(config.getQuit())
                .append("; start: ")
                .append(config.getStart())
                .append("; nav:")
                .append(config.getUp())
                .append("/")
                .append(config.getDown())
                .toString();
    }

    String getGameFooter(){
        return new StringBuilder()
                .append("control (ULDR): ")
                .append(config.getUp())
                .append(config.getLeft())
                .append(config.getDown())
                .append(config.getRight())
                .append("; stop: ")
                .append(config.getStop())
                .append("; main menu: ")
                .append(config.getRet())
                .toString();
    }
}
