package me.hikari.snakeclient;

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import me.hikari.snakeclient.ctl.GameManager;
import me.hikari.snakeclient.data.config.GameConfig;
import me.hikari.snakeclient.tui.Tui;

import java.io.File;
import java.io.IOException;

public class Main {
    private static String configName = "./src/main/resources/config.yaml";

    private static void formConfigPath(String name) {
        configName = "./" + name + ".yaml";
    }

    public static GameConfig parseConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.findAndRegisterModules();
        var config = mapper.readValue(new File(configName), GameConfig.class);
        System.out.println(config);
        return config;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            formConfigPath(args[0]);
        }
        Tui tui = new Tui();
        GameManager manager = new GameManager(tui, parseConfig());
        manager.start();
    }
}
