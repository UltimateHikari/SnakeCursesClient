package me.hikari.snakeclient.tui;

import com.googlecode.lanterna.TextColor;
import me.hikari.snakeclient.data.Player;

import java.util.*;

class Brush {
    private Map<Player, TextColor> colors = new HashMap<>();
    private List<TextColor> unused;

    private void initUnused(){
        // no YELLOW_BRIGHT
        // Array list bc should be able to remove in get Color
        unused = new ArrayList<>(Arrays.asList(
                TextColor.ANSI.MAGENTA,
                TextColor.ANSI.BLUE,
                TextColor.ANSI.CYAN,
                TextColor.ANSI.GREEN,
                TextColor.ANSI.RED
        ));
    }

    public Brush(){
        initUnused();
    }

    public TextColor getColor(Player p){
        if(!colors.containsKey(p)){
            if(unused.size() == 0){
                initUnused();
            }
            Collections.shuffle(unused);
            colors.put(p, unused.remove(unused.size() - 1));
        }
        return colors.get(p);
    }
}
