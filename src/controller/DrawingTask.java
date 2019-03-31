package controller;

import javafx.scene.paint.Color;
import layout.Graphic;
import model.Function;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DrawingTask implements Runnable {
    private final Function function;
    private final Graphic graphic;


    public DrawingTask(Function function, Graphic graphic){
        this.function = function;
        this.graphic = graphic;
    }

    @Override
    public void run() {
        int colorBound = 2;
        int functionPointsIter = 0;

        /*Color color = Color.color(
                new Random().nextInt(colorBound),
                new Random().nextInt(colorBound),
                new Random().nextInt(colorBound)
        );*/

        Color color = Color.RED;

        Lock lock = new ReentrantLock();

        while (!Thread.currentThread().isInterrupted()) {
            lock.lock();

            try {
                if (functionPointsIter < function.getPoints().size()) {
                    lock.lock();
                    graphic.setDrawingColor(color);
                    graphic.drawPoint(function.getPoints().get(functionPointsIter++));
                    lock.unlock();
                }
            } finally {
                lock.unlock();
            }
        }
    }
}