package me.hikari.snakeclient.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Player {
    private final String name;
    private final Integer id;
    private final String ip;
    private Integer score = 0;
    //color
    public Player(){
        this("host", 0, "localhost");
    }
    public void score(){
        score += 1;
    }
}
