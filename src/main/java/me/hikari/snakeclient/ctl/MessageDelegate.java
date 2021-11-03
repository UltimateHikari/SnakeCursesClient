package me.hikari.snakeclient.ctl;

import me.hikari.snakeclient.data.Peer;
import me.hikari.snakes.SnakesProto;

interface MessageDelegate {
    void joinAsNormal(Integer receiverId);

    void handleSteerMsg(SnakesProto.Direction direction, Peer peer);

    Integer handleJoinMsg(Peer peer, String name);

    Integer getLocalID();

    Integer getPeerID(Peer peer);

    void handleStateMsg(SnakesProto.GameState state);

    void handleErrorMsg(String errorMessage);

    void handleExitChange(Peer peer);

    void handleReceiverRoleChange(SnakesProto.NodeRole role);
}
