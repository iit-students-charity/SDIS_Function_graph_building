package controller;

import layout.Graphic;
import model.Function;

public class Controller {
    private Function function;
    private Graphic graphic;
    private Thread calcThread;
    private Thread drawThread;


    public Controller(Function function, Graphic graphic) {
        this.function = function;
        this.graphic = graphic;
        calcThread = new Thread();
        drawThread = new Thread();
    }

    public void startGraphicBuilding(int numberOfLists) {
        if (!calcThread.isAlive() || (calcThread.isInterrupted() && drawThread.isInterrupted())) {
            function.getPoints().clear();
            graphic.clear();

            drawThread = new Thread(new DrawingTask(function, graphic));
            calcThread = new Thread(new SortingTask(function, numberOfLists));
            calcThread.start();
            drawThread.start();
        }
    }

    public void stopGraphicBuilding() {
        if (!calcThread.isInterrupted() && !drawThread.isInterrupted()) {
            calcThread.interrupt();
            drawThread.interrupt();
            function.getPoints().clear();
        }
    }
}
