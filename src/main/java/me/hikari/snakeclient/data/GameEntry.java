package me.hikari.snakeclient.data;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakeclient.data.config.EngineConfig;

@AllArgsConstructor
@Getter
public class GameEntry implements UIGameEntry {
    private Player player;
    private EngineConfig config;
}
