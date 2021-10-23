package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

    public void replenishFood() {
        //TODO update algorithm for consulting with field
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

    public void applyMoves() {
        moves.forEach((Player p, Direction d) -> snakeMap.get(p).turnHead(new Coord(d)));
    }

    public void moveSnakes() {
        synchronized (mapMonitor) {
            var list = new ArrayList<MoveResult>();
            var worldSize = config.getWorldSize();
            var field = new FieldRepresentation(worldSize);
            snakeMap.forEach((Player p, Snake s) -> {
                list.add(new MoveResult(s.moveHead(worldSize), s));
                s.showYourself(field::putSnakeCell, worldSize);
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
