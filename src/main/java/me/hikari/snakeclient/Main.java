package me.hikari.snakeclient;

import me.hikari.snakeclient.ctl.GameManager;
import me.hikari.snakeclient.data.KeyConfig;
import me.hikari.snakeclient.tui.Tui;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        /**
         * TODO add passing default config file as arg
         * TODO config must have unique-city verification on parse
         */
        Tui tui = new Tui();
        KeyConfig keyConfig = new KeyConfig();
        GameManager manager = new GameManager(tui, keyConfig);
    }
}
