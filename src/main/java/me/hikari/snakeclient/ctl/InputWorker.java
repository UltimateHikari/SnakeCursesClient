package me.hikari.snakeclient.ctl;

import com.googlecode.lanterna.input.KeyStroke;
import lombok.RequiredArgsConstructor;
import me.hikari.snakeclient.data.Engine;

import java.io.IOException;
import java.util.function.Supplier;
@RequiredArgsConstructor
public class InputWorker implements Runnable{
    //TODO get back to synchronizer & supplier<KeyStroke>
    private final GameManager manager;

    @Override
    public void run() {
        while(true){
            try {
                KeyStroke stroke = manager.getUi().getInput();
                tryHandleStroke(stroke);
                System.out.println(stroke.getCharacter() + " " + stroke.getEventTime());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void tryHandleStroke(KeyStroke stroke) {
        switch (stroke.getCharacter()){
            case 'q', 'e':
                manager.getSynchronizer().switchActiveScreen();
                break;
            default:
                System.out.println(stroke.getCharacter());
        }
    }
}
