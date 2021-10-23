package me.hikari.snakeclient.data;

import java.util.Arrays;
import java.util.List;

public class FieldRepresentation {
    private enum CellType{
        EMPTY,
        SNAKE,
        FOOD,
        SNAKE_COL,
        FOOD_COL; //means snake ate food
    };
    private final CellType[] field;
    private final Integer xDim;

    FieldRepresentation(Coord dims, List<Coord> foods){
        xDim = dims.getX();
        field = new CellType[dims.getX()*dims.getY()];
        Arrays.fill(field, CellType.EMPTY);
        for(Coord c: foods){
            setCell(c, CellType.FOOD);
        }
    }

    private CellType getCell(Coord c){
        return field[xDim*c.getY() + c.getX()];
    }

    private void setCell(Coord c, CellType type){
        field[xDim*c.getY() + c.getX()] = type;
    }

    public void putSnakeCell(Coord c){
        switch (getCell(c)){
            case SNAKE_COL:
                break;
            case FOOD:
                setCell(c, CellType.FOOD_COL);
                break;
            case SNAKE, FOOD_COL:
                setCell(c, CellType.SNAKE_COL);
                break;
            default:
                setCell(c, CellType.SNAKE);
        }

    }

    public void putFoodCell(Coord c){
        setCell(c, CellType.FOOD);
    }

    public void dropTail(Coord c){
        setCell(c, CellType.EMPTY);
    }

    public boolean isCellSnakeCollided(Coord c){
        return getCell(c) == CellType.SNAKE_COL;
    }

    public boolean isCellFoodCollided(Coord c){
        return getCell(c) == CellType.FOOD_COL;
    }
}
