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

    /**
     * minor TODO
     * check canJoin
     */

    public GameEntry(SnakesProto.GameMessage.AnnouncementMsg msg, InetAddress address){
        joinAddress = address;
        this.config = new EngineConfig(msg.getConfig());
        for(SnakesProto.GamePlayer p : msg.getPlayers().getPlayersList()){
            players.add(new Player(p));
        }
    }

    public Player getMaster(){
        return players.stream().filter(Player::isMaster).findFirst().get();
    }

//    /**
//     * TODO
//     * refactor to getting host on tui side;
//     * left for compat reasons
//     */
//
//    public Player getPlayer(){
//        return players.get(0);
//    }
}
