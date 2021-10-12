package me.hikari.snakeclient.tui;

import me.hikari.snakeclient.data.Engine;

public interface ConnectableUI {
    void engineSubscribe(Engine engine);
    void engineUnsubscribe();
}
