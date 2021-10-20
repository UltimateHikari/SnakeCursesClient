package me.hikari.snakeclient;

import me.hikari.snakeclient.ctl.GameManager;
import me.hikari.snakeclient.tui.Tui;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        //TODO add passsing default config file as arg
        Tui tui = new Tui();
        GameManager manager = new GameManager(tui);
    }
}
