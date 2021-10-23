package me.hikari.snakeclient.ctl;

enum ActiveScreen {
    MAIN,
    GAME;
}

//enum FieldState {
//    LATEST,
//    LAGGING;
//}

public class StateSynchronizer {
    private ActiveScreen screen = ActiveScreen.MAIN;
    //private FieldState state = FieldState.LATEST;

//    public boolean isStateLagging() {
//        return FieldState.LAGGING == state;
//    }

    public boolean isScreenMain() {
        return screen == ActiveScreen.MAIN;
    }

//    public synchronized void switchFieldState() {
//        if (isStateLagging()) {
//            state = FieldState.LATEST;
//        } else {
//            state = FieldState.LAGGING;
//        }
//    }

    public synchronized void switchActiveScreen() {
        if (isScreenMain()) {
            screen = ActiveScreen.GAME;
        } else {
            screen = ActiveScreen.MAIN;
        }
    }
}
