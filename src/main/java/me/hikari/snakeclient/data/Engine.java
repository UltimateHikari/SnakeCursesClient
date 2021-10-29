package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakes.SnakesProto;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Engine {
    private final Object stateLock = new Object();
    private Integer stateOrder = 0;
    private final static Integer PRECISION = 10000;

    private boolean isLatest = false;
    private EngineDTO dto = null;

    private EngineConfig config;

    private List<Player> players = new ArrayList<>();
    private List<Snake> snakes = new LinkedList<>();
    private Map<Integer, SnakesProto.Direction> moves = new HashMap<>();
    private List<Coord> foods = new ArrayList<>();
    private final Player host;

    @Synchronized("stateLock")
    public EngineDTO getDTO() {
        if (!isLatest) {
            dto = new EngineDTO(
                    stateOrder,
                    snakes.stream().map(s -> (UISnake) new Snake(s)).toList(),
                    players.stream().toList(),
                    foods.stream().toList(),
                    config
            );
            isLatest = true;
        }
        return dto;
    }

    @AllArgsConstructor
    private class MoveResult {
        @Getter
        private final Coord coord;
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
        this.snakes.add(spawnSnake(player.getId()));
        this.players.add(player);
    }

    /**
     * TODO
     * update spawning algo,
     * pass player id
     */

    private Snake spawnSnake(Integer id) {

        return new Snake(id, new Coord(SnakesProto.Direction.RIGHT),
                new Coord(5, 5), new Coord(SnakesProto.Direction.LEFT));
    }

    public void noteHostMove(SnakesProto.Direction move) {
        notePlayerMove(host, move);
    }

    private void notePlayerMove(Player player, SnakesProto.Direction move) {
        moves.put(player.getId(), move);
    }

    public void notePeerMove(Peer peer, SnakesProto.Direction move) {
        var player = players.stream().filter(p -> peer.equals(p)).findFirst();
        player.ifPresent(value -> notePlayerMove(value, move));
    }

    /**
     * TODO
     * update food spawn algo
     */

    @Synchronized("stateLock")
    private void replenishFood() {
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

    public void removeFood(Coord f) {
        for (int i = 0; i < foods.size(); i++) {
            if (f.equals(foods.get(i))) {
                foods.remove(i);
            }
        }
    }

    public void doStep() {
        replenishFood();
        applyMoves();
        moveSnakes();
    }

    private void applyMoves() {
        moves.forEach((Integer p, SnakesProto.Direction d) -> {
            var snake = snakes.stream().filter(s -> s.getPlayerID().equals(p)).findFirst();
            snake.ifPresent(value -> value.turnHead(new Coord(d)));
        });
        moves.clear();
    }

    @Synchronized("stateLock")
    public void applyState(SnakesProto.GameState gameState) {
        this.stateOrder = gameState.getStateOrder();
        this.foods = gameState
                .getFoodsList()
                .stream().map(Coord::new).toList();
        this.players = gameState
                .getPlayers().getPlayersList()
                .stream().map(Player::new).toList();
        this.snakes = gameState
                .getSnakesList()
                .stream().map(Snake::new).toList();
        this.config = new EngineConfig(gameState.getConfig());
    }

    /**
     * TODO
     * add score for murderer in collision
     */

    @Synchronized("stateLock")
    private void moveSnakes() {
        var list = new ArrayList<MoveResult>();
        var worldSize = config.getWorldSize();
        var field = new FieldRepresentation(worldSize, foods);
        snakes.forEach(s -> s.showYourself(field::putSnakeCell, worldSize));
        snakes.forEach(s -> {
            var head = s.moveHead(worldSize);
            list.add(new MoveResult(head, s));
            field.putSnakeCell(head);
        });

        list.forEach((MoveResult m) -> {
            var head = m.getCoord();

            if (!field.isCellFoodCollided(m.getCoord())) {
                m.getSnake().dropTail();
            } else {
                var player = players.stream()
                        .filter(p -> p.getId().equals(m.getSnake().getPlayerID())).findFirst();
                if (player.isPresent()) {
                    player.get().score();
                }
                removeFood(m.getCoord());
            }

            if (field.isCellSnakeCollided(head)) {
                m.getSnake().showYourself(c -> {
                    if (c != head) {
                        this.spawnFoodWithProb(c);
                    }
                }, worldSize);
                snakes.remove(m.getSnake());
                return;
            }
        });
        replenishFood();
        isLatest = false;
        stateOrder++;

    }

    private void spawnFoodWithProb(Coord coord) {
        Random random = ThreadLocalRandom.current();
        if (config.getDeadFoodProb() > (float) random.nextInt(PRECISION) / PRECISION) {
            foods.add(coord);
        }
    }

}
