package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.data.config.UIConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Engine implements EngineGetter{
    private Integer stateOrder;
    private EngineConfig config;
    @Getter
    private final Map<Player, Snake> snakeMap = new HashMap<>();
    private final Map<Player, Direction> moves = new HashMap<>();
    private final List<Coord> foods = new ArrayList<>();
    private FieldRepresentation field;

    @AllArgsConstructor
    private class MoveResult{
        @Getter
        private final Coord coord;
        @Getter
        private final Snake snake;
    }

    public Engine(EngineConfig config){
        this.config = config;
        field = new FieldRepresentation(new Coord(config.getWidth(), config.getHeight()));
    }
    public void addPlayer(Player player){
        Snake snake = spawnSnake();
        snakeMap.put(player, snake);
    }

    private Snake spawnSnake(){
        //TODO upgrade algo
        return new Snake(Direction.RIGHT, new Coord(5,5), new Coord(-1,0));
    }

    public void notePlayerMove(Player player, Direction move){
        moves.put(player, move);
    }

    public UIConfig getUIConfig(){
        return config;
    }

    public void applyMoves() {
        moves.forEach((Player p, Direction d) -> snakeMap.get(p).turnHead(d));
    }

    public void moveSnakes() {
        ArrayList<MoveResult> list = new ArrayList<>();
        snakeMap.forEach((Player p, Snake s) -> {
            list.add(new MoveResult(s.moveHead(), s));
        });
        list.forEach((MoveResult m) -> {
            if(field.isCellSnakeCollided(m.getCoord())){
                // TODO kill all stuff
                return;
            }
            if(!field.isCellFoodCollided(m.getCoord())){
                m.getSnake().dropTail();
            }
        });
    }


}
