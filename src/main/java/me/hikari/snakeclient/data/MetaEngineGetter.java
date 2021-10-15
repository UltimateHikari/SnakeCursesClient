package me.hikari.snakeclient.data;

import me.hikari.snakeclient.data.config.EngineConfig;

import java.util.Set;

public interface MetaEngineGetter {
    Set<EngineConfig> getGames();
}
