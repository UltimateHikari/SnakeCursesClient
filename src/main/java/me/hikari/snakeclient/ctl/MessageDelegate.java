package me.hikari.snakeclient.ctl;

import me.hikari.snakeclient.data.Peer;
import me.hikari.snakes.SnakesProto;

interface MessageDelegate {
    void joinAsNormal(Integer receiverId);

    void handleSteerMsg(SnakesProto.Direction direction, Peer peer);

    Integer handleJoinMsg(Peer peer, String name);

    Integer getLocalID();

    void handleStateMsg(SnakesProto.GameState state);

    void handleErrorMsg(String errorMessage);

    void handleExitChange(Peer peer);

    Integer handleReceiverRoleChange(SnakesProto.NodeRole role);
}
