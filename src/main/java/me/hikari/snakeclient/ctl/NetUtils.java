package me.hikari.snakeclient.ctl;

import com.google.protobuf.InvalidProtocolBufferException;
import me.hikari.snakeclient.data.Peer;
import me.hikari.snakeclient.data.config.NetConfig;
import me.hikari.snakes.SnakesProto;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

class NetUtils {

    public static SnakesProto.GameMessage tryDeserializeGameMessage(
            DatagramPacket packet
    ) throws InvalidProtocolBufferException{
        var byteBuf = ByteBuffer.wrap(packet.getData());
        var len = byteBuf.getInt();
        var buf = new byte[len];
        byteBuf.get(buf, 0, len);
        return SnakesProto.GameMessage.parseFrom(buf);
    }

    public static byte [] serializeGameMessageBuf(byte [] buf, NetConfig config){
        return ByteBuffer.allocate(config.getMaxMsgSize()).putInt(buf.length).put(buf).array();
    }

    public static Peer getPeer(DatagramPacket packet) {
        return new Peer(packet.getAddress().getHostAddress(), packet.getPort());
    }

    public static InetSocketAddress packet2addr(DatagramPacket packet){
        return new InetSocketAddress(packet.getAddress().getHostAddress(), packet.getPort());
    }
}
