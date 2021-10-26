package me.hikari.snakeclient;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.hikari.snakeclient.ctl.GameManager;
import me.hikari.snakeclient.data.config.GameConfig;
import me.hikari.snakeclient.data.config.KeyConfig;
import me.hikari.snakeclient.tui.Tui;

import java.io.File;
import java.io.IOException;

public class Main {
    //TODO you too buddy
    private static final String addr = "239.1.1.1";
    private static final Integer port = 8080;

    public static void main(String[] args) throws IOException {
        /**
         * TODO add passing default config file as arg
         * TODO config must have unique-city verification on parse
         */
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.findAndRegisterModules();
        GameConfig config = mapper.readValue(new File("src/main/resources/config.yaml"), GameConfig.class);
        System.out.println(config);
//        Tui tui = new Tui();
//        KeyConfig keyConfig = new KeyConfig();
//        GameManager manager = new GameManager(tui, keyConfig, addr, port, Integer.valueOf(args[0]));
//        manager.start();
    }
}
