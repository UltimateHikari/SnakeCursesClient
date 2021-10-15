package me.hikari.snakeclient.data;

import me.hikari.snakeclient.data.config.EngineConfig;

import java.util.*;
import java.util.stream.Collectors;

public class MetaEngine implements MetaEngineGetter {
    private final static Integer GAME_KEEP_ALIVE_MS = 5000;
    private final Object mapMonitor = new Object();
    private Map<EngineConfig, Long> games = new HashMap<>();

    public Set<EngineConfig> getGames() {
        Set<EngineConfig> config;
        synchronized (mapMonitor) {
            config = new TreeSet<>(games.keySet());
        }
        return config;
    }

    public void addGame(EngineConfig config) {
        synchronized (mapMonitor) {
            long time = System.currentTimeMillis();
            games.put(config, time);
        }
    }

    public void actualizeGames() {
        synchronized (mapMonitor) {
            long time = System.currentTimeMillis();
            games = games.entrySet()
                    .stream()
                    .filter(map -> (time - map.getValue()) < GAME_KEEP_ALIVE_MS)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
