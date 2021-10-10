package me.hikari.snakeclient.data;

import java.util.ArrayList;
import java.util.List;

public class Engine {
    private Integer stateOrder;
    //TODO list -> map
    private List<Snake> snakes = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private List<Coord> foods = new ArrayList<>();

    public Engine(){
    }
    public void addPlayer(Player player){
        players.add(player);
    }

    public void notePlayerMove(int id, Move move){
    }
}
