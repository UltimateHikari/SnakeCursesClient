package me.hikari.snakeclient.data;

import java.util.Arrays;

public class FieldRepresentation {
    private enum CellType{
        EMPTY,
        SNAKE,
        FOOD,
        SNAKE_COL,
        FOOD_COL;
    };
    private final CellType[] field;
    private final Integer xDim;

    FieldRepresentation(Coord dims){
        xDim = dims.getX();
        field = new CellType[dims.getX()*dims.getY()];
        Arrays.fill(field, CellType.EMPTY);
    }

    private CellType getCell(Coord c){
        return field[xDim*c.getX() + c.getY()];
    }

    private void setCell(Coord c, CellType type){
        field[xDim*c.getX() + c.getY()] = type;
    }

    public void putSnakeCell(Coord c){
        setCell(c, CellType.SNAKE);
    }

    public void putFoodCell(Coord c){
        setCell(c, CellType.FOOD);
    }

    public boolean isCellSnakeCollided(Coord c){
        return getCell(c) == CellType.SNAKE_COL;
    }

    public boolean isCellFoodCollided(Coord c){
        return getCell(c) == CellType.FOOD_COL;
    }
}
