package me.hikari.snakeclient.data;

import me.hikari.snakeclient.data.config.EngineConfig;

import java.util.*;
import java.util.stream.Collectors;

public class MetaEngine{
    private final static Integer GAME_KEEP_ALIVE_MS = 5000;
    private final Object mapMonitor = new Object();
    private Map<EngineConfig, Long> games = new HashMap<>();

    private boolean isLatest = false;
    private MetaEngineDTO dto;

    public MetaEngineDTO getDTO() {
        Set<EngineConfig> config;
        synchronized (mapMonitor) {
            if(!isLatest) {
                config = new TreeSet<>(games.keySet());
                isLatest = true;
                dto = new MetaEngineDTO(config);
            }
            return dto;
        }
    }

    public void addGame(EngineConfig config) {
        synchronized (mapMonitor) {
            long time = System.currentTimeMillis();
            games.put(config, time);
            isLatest = false;
        }
    }

    public void actualizeGames() {
        synchronized (mapMonitor) {
            long time = System.currentTimeMillis();
            games = games.entrySet()
                    .stream()
                    .filter(map -> (time - map.getValue()) < GAME_KEEP_ALIVE_MS)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            isLatest = false;
        }
    }
}
