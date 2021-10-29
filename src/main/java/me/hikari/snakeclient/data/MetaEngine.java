package me.hikari.snakeclient.data;

import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.config.EngineConfig;

import java.util.*;
import java.util.stream.Collectors;

public class MetaEngine {
    public final static Integer GAME_KEEP_ALIVE_MS = 5000;
    private final Object mapMonitor = new Object();
    private Map<GameEntry, Long> games = new HashMap<>();

    private boolean isLatest = false;
    private MetaEngineDTO dto;
    private final GameEntry defaultEntry;
    private GameEntry selectedEntry = null;
    private int selectedIndex = 0;

    public MetaEngine(GameEntry entry){
        this.defaultEntry = entry;
        refreshSelectedEntry();
    }

    public MetaEngineDTO getDTO() {
        Set<UIGameEntry> config;
        synchronized (mapMonitor) {
            if (!isLatest) {
                config = new HashSet<>(games.keySet());
                isLatest = true;
                var selectedDTO = selectedEntry;
                dto = new MetaEngineDTO(defaultEntry, config, selectedDTO);
            }
            return dto;
        }
    }

    public void addGame(GameEntry entry) {
        synchronized (mapMonitor) {
            // TODO check on duplicates from that player
            long time = System.currentTimeMillis();
            for(GameEntry e : games.keySet()){
                if(e.equals(entry)){
                    return;
                }
            }
            games.put(entry, time);
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

    private void refreshSelectedEntry(){
        if (selectedIndex == 0) {
            selectedEntry = defaultEntry;
        } else {
            var it = games.keySet().iterator();
            var i = 1;
            while(i != selectedIndex && it.hasNext()){
                it.next();
            }
            selectedEntry = it.next();
        }
        isLatest = false;
    }

    public void navDown() {
        synchronized (mapMonitor) {
            var size = games.size() + 1;
            selectedIndex = (selectedIndex - 1 + size) % size;
            refreshSelectedEntry();
        }
    }

    public void navUp() {
        synchronized (mapMonitor) {
            var size = games.size() + 1;
            selectedIndex = (selectedIndex + 1) % size;
            refreshSelectedEntry();
        }
    }

    public GameEntry getSelectedEntry() {
        return selectedEntry;
    }
}
