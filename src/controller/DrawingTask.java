package controller;

import javafx.scene.paint.Color;
import layout.Graphic;
import model.Function;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DrawingTask implements Runnable {
    private final Graphic graphic;


    public DrawingTask(Graphic graphic){
        this.graphic = graphic;
    }

    @Override
    public void run() {
        Lock lock = new ReentrantLock();

        while (!Thread.currentThread().isInterrupted()) {
            graphic.update();
        }
    }
}