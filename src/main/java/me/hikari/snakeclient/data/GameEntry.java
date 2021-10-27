package me.hikari.snakeclient.data;


import lombok.Getter;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakes.SnakesProto;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class GameEntry implements UIGameEntry {
    private List<Player> players = new ArrayList<>();
    @Getter
    private EngineConfig config;
    @Getter
    private InetAddress joinAddress = null;

    public GameEntry(Player player, EngineConfig config){
        players.add(player);
        this.config = config;
    }

    public GameEntry(SnakesProto.GameMessage.AnnouncementMsg msg, InetAddress address){
        joinAddress = address;
        this.config = new EngineConfig(msg.getConfig());
        for(SnakesProto.GamePlayer p : msg.getPlayers().getPlayersList()){
            players.add(new Player(p));
        }
        // TODO stop ignoring canjoin
    }

    public Player getPlayer(){
        // TODO for compat reasons, refactor this and consumers of this
        return players.get(0);
    }
}
