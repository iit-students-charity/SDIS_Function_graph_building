package controller;

import javafx.scene.paint.Color;
import layout.Graphic;
import model.Function;

import java.util.Random;

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

        graphic.setDrawingColor(Color.color(
                new Random().nextInt(colorBound),
                new Random().nextInt(colorBound),
                new Random().nextInt(colorBound)
        ));

        while (!Thread.currentThread().isInterrupted()) {
            synchronized (function) {
                if (functionPointsIter < function.getPoints().size()) {
                    synchronized (graphic) {
                        graphic.drawPoint(function.getPoints().get(functionPointsIter++));
                    }
                }

                try {
                    function.wait();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}