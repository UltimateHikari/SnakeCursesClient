package me.hikari.snakeclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import lombok.extern.log4j.Log4j2;
import me.hikari.snakeclient.ctl.Game;
import me.hikari.snakeclient.data.config.GameConfig;
import me.hikari.snakeclient.tui.Tui;

import java.io.File;
import java.io.IOException;

@Log4j2
public class Main {
    private static String configName = "./src/main/resources/config.yaml";

    private static void formConfigPath(String name) {
        configName = "./" + name + ".yaml";
    }

    public static GameConfig parseConfig() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.findAndRegisterModules();
        var config = mapper.readValue(new File(configName), GameConfig.class);
        log.info(config);
        return config;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 0) {
            formConfigPath(args[0]);
        }else{
            log.info("No config provided");
            System.exit(-1);
        }
        var config = parseConfig();
        Tui tui = new Tui(config.getKeyConfig());
        Game game = new Game(tui, config);
        game.start();
    }
}
