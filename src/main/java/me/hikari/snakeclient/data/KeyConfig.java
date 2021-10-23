package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeyConfig {
    //primitives for easier comparison outside
    private final char quit;
    private final char start;
    private final char stop;
    private final char ret;
    private final char up;
    private final char down;
    private final char left;
    private final char right;
    public KeyConfig(){
        this('q','g','h','e','w','s','a','d');
    }
}
