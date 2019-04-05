package controller;

import layout.GraphicCanvas;
import model.Function;


public class Controller {private Function arrayFunction;
    private Function linearFunction;
    private GraphicCanvas graphicCanvas;
    private Thread arrayFunCalcThread;
    private Thread linFunCalcThread;

    private boolean isTCalcThreadsAlive;


    public Controller(Function arrayFunction, Function linearFunction, GraphicCanvas graphic) {
        this.arrayFunction = arrayFunction;
        this.linearFunction = linearFunction;
        this.graphicCanvas = graphic;
        arrayFunCalcThread = new Thread("array-calc");
        linFunCalcThread = new Thread("linear-calc");

        Thread drawThread = new Thread(new DrawingTask(graphic));
        drawThread.setName("draw");
        drawThread.setDaemon(true);
        drawThread.start();

        isTCalcThreadsAlive = false;
    }

    public void startGraphicBuilding(int numberOfLists) {
        if (!isTCalcThreadsAlive) {
            arrayFunction.getPoints().clear();
            linearFunction.getPoints().clear();
            graphicCanvas.eraseCanvas();

            arrayFunCalcThread = new Thread(new SortingTask(arrayFunction, numberOfLists));
            linFunCalcThread = new Thread(new LinearFunctionCalcTask(linearFunction));

            arrayFunCalcThread.setDaemon(true);
            linFunCalcThread.setDaemon(true);

            arrayFunCalcThread.start();
            linFunCalcThread.start();

            isTCalcThreadsAlive = true;
        }
    }

    public void incrementGraphicScale() {
        graphicCanvas.incrementScale();
    }

    public void decrementGraphicScale() {
        graphicCanvas.decrementScale();
    }

    public void stopGraphicBuilding() {
        if (isTCalcThreadsAlive) {
            arrayFunCalcThread.interrupt();
            linFunCalcThread.interrupt();
            isTCalcThreadsAlive = false;
        }
    }
}
