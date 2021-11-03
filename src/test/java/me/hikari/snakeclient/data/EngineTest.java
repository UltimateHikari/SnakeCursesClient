package me.hikari.snakeclient.data;

import junit.framework.TestCase;
import lombok.extern.log4j.Log4j2;
import me.hikari.snakeclient.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class EngineTest {

    @Test
    public void testPlayerAddition() throws IOException {
        var config = Main.parseConfig();
        var world = config.getEngineConfig().getWorldSize();
        var maxSnakes = world.getX() * world.getY() / 9;
        var player = new Player(config.getPlayerConfig());
        var engine = new Engine(new GameEntry(player, config.getEngineConfig()), player, null);
        Boolean res = true;
        for (int i = 1; i < maxSnakes; i++){
            res = engine.addPlayer(player);
        }
        Assert.assertEquals(res, false);
    }
}