package controller;

import layout.Graphic;
import model.Function;

public class DrawingTask implements Runnable {
    private final Function function;
    private Graphic graphic;


    public DrawingTask(Function function, Graphic graphic){
        this.function = function;
        this.graphic = graphic;
    }

    @Override
    public void run() {
        int functionPointsIter = 0;
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (function) {
                if (functionPointsIter < function.getPoints().size()) {
                    graphic.addPoint(function.getPoints().get(functionPointsIter++));
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