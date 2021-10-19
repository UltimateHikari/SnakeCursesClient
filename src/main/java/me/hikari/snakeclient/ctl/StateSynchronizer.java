package me.hikari.snakeclient.ctl;

enum ActiveScreen {
    MAIN,
    GAME;
}

enum FieldState {
    LATEST,
    LAGGING;
}

public class StateSynchronizer {
    private ActiveScreen screen = ActiveScreen.MAIN;
    private FieldState state = FieldState.LATEST;
    private NavDirection direction = NavDirection.NEUTRAL;

    public boolean isStateLagging() {
        return FieldState.LAGGING == state;
    }

    public boolean isScreenMain() {
        return screen == ActiveScreen.MAIN;
    }

    public synchronized void switchFieldState() {
        if (isStateLagging()) {
            state = FieldState.LATEST;
        } else {
            state = FieldState.LAGGING;
        }
    }

    public synchronized void switchActiveScreen() {
        if (isScreenMain()) {
            screen = ActiveScreen.GAME;
        } else {
            screen = ActiveScreen.MAIN;
        }
    }

    public NavDirection popNavDirection(){
        var pop = direction;
        direction = NavDirection.NEUTRAL;
        return pop;
    }

    public void NavUp(){
        direction = NavDirection.UP;
    }

    public void NavDown(){
        direction = NavDirection.DOWN;
    }
}
