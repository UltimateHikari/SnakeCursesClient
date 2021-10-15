package me.hikari.snakeclient.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Player {
    private final String name;
    private final Integer id;
    private Integer score = 0;
    //color
    public Player(){
        this("host", 0);
    }
}
