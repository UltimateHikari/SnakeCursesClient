package me.hikari.snakeclient;

import org.junit.Test;

import java.io.IOException;

public class MainTest {

    @Test(expected = Test.None.class)
    public void testParseConfig() throws IOException {
        var config = Main.parseConfig();
    }
}