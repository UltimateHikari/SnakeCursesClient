package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakeclient.data.config.UIConfig;

import java.util.*;

public class Engine {
    private Integer stateOrder = 0;

    private boolean isLatest = false;
    private EngineDTO dto = null;
    private final Object mapMonitor = new Object();

    private EngineConfig config;
    @Getter
    private final Map<Player, Snake> snakeMap = new HashMap<>();
    private final Map<Player, Direction> moves = new HashMap<>();
    private final List<Coord> foods = new ArrayList<>();

    public EngineDTO getDTO() {
        synchronized (mapMonitor) {
            if (!isLatest) {
                var map = new HashMap<Player, Snake>();
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
        private final Snake snake;
    }

    public Engine(GameEntry entry) {
        this.config = entry.getConfig();
        addPlayer(entry.getPlayer());
        replenishFood();
    }

    public void addPlayer(Player player) {
        Snake snake = spawnSnake();
        snakeMap.put(player, snake);
    }

    private Snake spawnSnake() {
        //TODO upgrade algo
        return new Snake(new Coord(Direction.RIGHT), new Coord(5, 5), new Coord(Direction.LEFT));
    }

    public void notePlayerMove(Player player, Direction move) {
        moves.put(player, move);
    }

    public UIConfig getUIConfig() {
        return config;
    }

    public void replenishFood(){
        //TODO update algoritm for consulting with field
        synchronized (mapMonitor) {
            var r = new Random();
            while (foods.size() < config.getFoodStatic()){
                foods.add(new Coord(r.nextInt(config.getWidth()), r.nextInt(config.getHeight())));
            }
        }
    }

    public void applyMoves() {
        moves.forEach((Player p, Direction d) -> snakeMap.get(p).turnHead(new Coord(d)));
    }

    public void moveSnakes() {
        synchronized (mapMonitor) {
            var list = new ArrayList<MoveResult>();
            var field = new FieldRepresentation(new Coord(config.getWidth(), config.getHeight()));
            snakeMap.forEach((Player p, Snake s) -> {
                list.add(new MoveResult(s.moveHead(config.worldSize()), s));
                s.showYourself(c -> field.putSnakeCell(c), config.worldSize());
            });
            list.forEach((MoveResult m) -> {
                if (field.isCellSnakeCollided(m.getCoord())) {
                    // TODO kill all stuff smh
                    return;
                }
                if (!field.isCellFoodCollided(m.getCoord())) {
                    m.getSnake().dropTail();
                }
            });
            isLatest = false;
            stateOrder++;
        }
    }


}
