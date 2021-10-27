package me.hikari.snakeclient;

import junit.framework.TestCase;
import me.hikari.snakeclient.data.config.GameConfig;
import org.junit.Test;

import java.io.IOException;

public class MainTest extends TestCase {

    @Test(expected = Test.None.class)
    public void testParseConfig() throws IOException {
        GameConfig config = Main.parseConfig();
    }
}