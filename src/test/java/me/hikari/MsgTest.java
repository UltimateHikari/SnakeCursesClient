package me.hikari;

import com.google.protobuf.InvalidProtocolBufferException;
import me.hikari.snakes.SnakesProto;
import org.junit.Assert;
import org.junit.Test;

public class MsgTest {

    @Test
    public void BuildTest(){
        var ack = SnakesProto.GameMessage.AckMsg.newBuilder().build();
        var msg = SnakesProto.GameMessage.newBuilder().setAck(ack).setMsgSeq(1).build();
        Assert.assertEquals(1, msg.getMsgSeq());
    }

    @Test
    public void ReBuildTest(){
        var ack = SnakesProto.GameMessage.AckMsg.newBuilder().build();
        var msg = SnakesProto.GameMessage.newBuilder().setAck(ack).setMsgSeq(1).build();
        var msg2 = msg.toBuilder().setMsgSeq(2).build();
        Assert.assertEquals(2, msg2.getMsgSeq());
    }

    @Test
    public void PartialBuildTest() throws InvalidProtocolBufferException {
        var ack = SnakesProto.GameMessage.AckMsg.newBuilder().build();
        var msg = SnakesProto.GameMessage.newBuilder().setAck(ack).buildPartial();
        var msgComp = msg.toBuilder().setMsgSeq(1).build();
        var res = SnakesProto.GameMessage.parseFrom(msgComp.toByteArray());
        Assert.assertEquals(1, msgComp.getMsgSeq());
    }
}
