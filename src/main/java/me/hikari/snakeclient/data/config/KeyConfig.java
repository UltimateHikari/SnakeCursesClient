package me.hikari.snakeclient.data.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class KeyConfig {
    //primitives for easier comparison outside
    private char quit;
    private char start;
    private char stop;
    private char ret;
    private char up;
    private char down;
    private char left;
    private char right;

    @JsonCreator
    public KeyConfig(
             @JsonProperty("quit") char quit,
             @JsonProperty("start") char start,
             @JsonProperty("stop") char stop,
             @JsonProperty("ret") char ret,
             @JsonProperty("up") char up,
             @JsonProperty("down") char down,
             @JsonProperty("left") char left,
             @JsonProperty("right") char right) {
    }

    @Override
    public String toString() {
        return "{" + quit + start + stop + ret + up + down + left + right + "}";
    }
}
