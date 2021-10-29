package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakes.SnakesProto;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Engine {
    private Integer stateOrder = 0;
    private final static Integer PRECISION = 10000;

    private boolean isLatest = false;
    //private final boolean isLocal;
    private EngineDTO dto = null;
    private final Object mapMonitor = new Object();

    private EngineConfig config;
    @Getter
    private final Map<Player, Snake> snakeMap = new HashMap<>();
    private final Map<Player, SnakesProto.Direction> moves = new HashMap<>();
    private final List<Coord> foods = new ArrayList<>();
    private final Player host;

    public EngineDTO getDTO() {
        synchronized (mapMonitor) {
            if (!isLatest) {
                var map = new HashMap<Player, UISnake>();
                snakeMap.forEach((p, s) -> map.put(p, new Snake(s)));
                var foodsDto = new ArrayList<>(foods);
                dto = new EngineDTO(map, foodsDto, config);
                isLatest = true;
            }
            return dto;
        }
    }

    @AllArgsConstructor
    private class MoveResult {
        @Getter
        private final Coord coord;
        @Getter
        private final Player player;
        @Getter
        private final Snake snake;
    }

    /**
     * TODO::Commworker.handleJoin
     */

    public Engine(GameEntry entry) {
        this.config = entry.getConfig();
        this.host = entry.getMaster();
        addPlayer(entry.getMaster());
        replenishFood();
    }

    public void addPlayer(Player player) {
        Snake snake = spawnSnake();
        snakeMap.put(player, snake);
    }

    /**
     * TODO
     * update spawning algo
     */

    private Snake spawnSnake() {

        return new Snake(new Coord(SnakesProto.Direction.RIGHT), new Coord(5, 5), new Coord(SnakesProto.Direction.LEFT));
    }

    public void noteHostMove(SnakesProto.Direction move) {
        notePlayerMove(host, move);
    }
    public void notePlayerMove(Player player, SnakesProto.Direction move) {
        moves.put(player, move);
    }
    public void notePeerMove(Peer peer, SnakesProto.Direction move){
        var player = snakeMap.keySet().stream().filter(p -> peer.equals(p)).findFirst();
        if(player.isPresent()){
            notePlayerMove(player.get(), move);
        }
    }

    /**
     * TODO
     * update food spawn algo
     */

    private void replenishFood() {
        synchronized (mapMonitor) {
            var r = new Random();
            while (foods.size() < config.getFoodStatic()) {
                foods.add(
                        new Coord(
                                r.nextInt(config.getWorldSize().getX()),
                                r.nextInt(config.getWorldSize().getY())
                        )
                );
            }
        }
    }

    public void removeFood(Coord f){
        for(int i = 0; i < foods.size(); i++){
            if(f.equals(foods.get(i))){
                foods.remove(i);
            }
        }
    }

    public void doStep(){
        replenishFood();
        applyMoves();
        moveSnakes();
    }

    private void applyMoves() {
        moves.forEach((Player p, SnakesProto.Direction d) -> snakeMap.get(p).turnHead(new Coord(d)));
        moves.clear();
    }

    public void applyState() {

    }

    public SnakesProto.GameMessage.StateMsg retrieveState(){
        return null;
    }

    /**
     * TODO
     * change logic in list.forEach
     * to logic from snakes.txt
     */

    private void moveSnakes() {
        synchronized (mapMonitor) {
            var list = new ArrayList<MoveResult>();
            var worldSize = config.getWorldSize();
            var field = new FieldRepresentation(worldSize, foods);
            snakeMap.forEach((Player p, Snake s) -> {
                if(!s.isDead()) {
                    list.add(new MoveResult(s.moveHead(worldSize), p, s));
                    s.showYourself(field::putSnakeCell, worldSize);
                }
            });
            list.forEach((MoveResult m) -> {
                if (field.isCellSnakeCollided(m.getCoord())) {
                    m.getSnake().showYourself(this::spawnFoodWithProb, worldSize);
                    m.getSnake().die();
                    return;
                }
                if (!field.isCellFoodCollided(m.getCoord())) {
                    m.getSnake().dropTail();
                } else {
                    m.getPlayer().score();
                    removeFood(m.getCoord());
                }
            });
            replenishFood();
            isLatest = false;
            stateOrder++;
        }
    }

    private void spawnFoodWithProb(Coord coord) {
        Random random = ThreadLocalRandom.current();
        if(config.getDeadFoodProb() > (float)random.nextInt(PRECISION)/PRECISION){
            foods.add(coord);
        }
    }

}
