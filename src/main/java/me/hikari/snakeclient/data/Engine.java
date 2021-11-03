package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Synchronized;
import me.hikari.snakeclient.ctl.Communicator;
import me.hikari.snakeclient.data.config.EngineConfig;
import me.hikari.snakes.SnakesProto;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    private final Map<Integer, SnakesProto.Direction> moves = new HashMap<>();
    private List<Coord> foods = new ArrayList<>();
    private final Player localPlayer;
    private final Communicator communicator;
    private String error = null;

    @Synchronized("stateLock")
    public EngineDTO getDTO() {
        if (!isLatest) {
            dto = new EngineDTO(
                    stateOrder,
                    snakes.stream().map(s -> (UISnake) new Snake(s)).toList(),
                    players.stream().toList(),
                    foods.stream().toList(),
                    config,
                    error
            );
            isLatest = true;
        }
        return dto;
    }

    public Integer joinPlayer(Peer peer, String name) {
        Integer id = players.stream()
                .max(Comparator.comparing(Player::getId))
                .get().getId() + 1;
        Player newcomer = new Player(peer, name, id);
        addPlayer(newcomer);
        return id;
    }

    public void sendSteer(SnakesProto.Direction direction) throws IOException {
        var steer = SnakesProto.GameMessage.SteerMsg.newBuilder()
                .setDirection(direction).build();
        var msg = SnakesProto.GameMessage.newBuilder()
                .setSteer(steer).buildPartial();
        communicator.sendMessageToMaster(msg);
    }

    @Synchronized("stateLock")
    public void noteError(String errorMessage) {
        this.error = errorMessage;
        isLatest = false;
    }

    @Synchronized("stateLock")
    public void setSelfRole(SnakesProto.NodeRole role) {
        localPlayer.setRole(role);
    }

    @AllArgsConstructor
    private class MoveResult {
        @Getter
        private final Coord coord;
        @Getter
        private final Snake snake;
    }

    public Engine(GameEntry entry, Player localPlayer, Communicator communicator) {
        this.config = entry.getConfig();
        this.localPlayer = localPlayer;
        this.communicator = communicator;
        if (localPlayer.isMaster()) {
            addPlayer(entry.getMaster());
        }
    }

    @Synchronized("stateLock")
    public void addPlayer(Player player) {
        this.snakes.add(spawnSnake(player.getId()));
        this.players.add(player);
    }

    /**
     * TODO
     * update spawning algo,
     */

    private Snake spawnSnake(Integer id) {

        return new Snake(id, new Coord(SnakesProto.Direction.RIGHT),
                new Coord(5, 5), new Coord(SnakesProto.Direction.LEFT));
    }

    public void noteHostMove(SnakesProto.Direction move) {
        notePlayerMove(localPlayer, move);
    }

    @Synchronized("stateLock")
    private void notePlayerMove(Player player, SnakesProto.Direction move) {
        moves.put(player.getId(), move);
    }

    public void notePeerMove(Peer peer, SnakesProto.Direction move) {
        findPeer(peer).ifPresent(value -> notePlayerMove(value, move));
    }

    private Optional<Player> findPeer(Peer peer){
        return players.stream().filter(peer::equals).findFirst();
    }

    @Synchronized("stateLock")
    public void exilePlayer(Peer peer) {
        findPeer(peer).ifPresent(value -> value.setRole(SnakesProto.NodeRole.VIEWER));
    }

    public Integer getPeerID(Peer peer) {
        var opt = findPeer(peer);
        if(opt.isPresent()){
            return opt.get().getId();
        }
        return 0;
    }

    /**
     * TODO
     * update food spawn algo
     */

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
                return;
            }
        }
    }

    @Synchronized("stateLock")
    public void doStep() throws IOException {
        if (localPlayer.isMaster()) {
            replenishFood();
            applyMoves();
            moveSnakes();
            propagateState();
        }
    }

    private void propagateState() throws IOException {
        var msg = SnakesProto.GameMessage
                .newBuilder()
                .setState(
                        getDTO().retrieveState()
                )
                .buildPartial();

        for (Player p : players) {
            if (!localPlayer.equals(p)) {
                var addr = new InetSocketAddress(InetAddress.getByName(p.getIp()), p.getPort());
                communicator.sendMessage(msg, addr);
            }
        }
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
        if (gameState.getStateOrder() > stateOrder) {
            this.stateOrder = gameState.getStateOrder();
            this.foods = gameState
                    .getFoodsList()
                    .stream().map(Coord::new).toList();
            this.players = gameState
                    .getPlayers().getPlayersList()
                    .stream().map(Player::new).toList();
            this.snakes = gameState
                    .getSnakesList()
                    .stream().map(Snake::new).collect(Collectors.toCollection(LinkedList<Snake>::new));
            this.config = new EngineConfig(gameState.getConfig());
            this.isLatest = false;
        }
    }

    /**
     * TODO
     * add score for murderer in collision
     */

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
                player.ifPresent(Player::score);
                removeFood(m.getCoord());
            }

            if (field.isCellSnakeCollided(head)) {
                m.getSnake().showYourself(c -> {
                    if (c != head) {
                        this.spawnFoodWithProb(c);
                    }
                }, worldSize);
                snakes.remove(m.getSnake());
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
