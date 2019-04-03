package controller;

import layout.GraphicCanvas;
import model.Function;


public class Controller {private Function arrayFunction;
    private Function linearFunction;
    private GraphicCanvas graphicCanvas;
    private Thread arrayFunCalcThread;
    private Thread linFunCalcThread;

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
    }

    public void startGraphicBuilding(int numberOfLists) {
        if (!arrayFunCalcThread.isAlive() || arrayFunCalcThread.isInterrupted()) {
            arrayFunction.getPoints().clear();
            linearFunction.getPoints().clear();
            graphicCanvas.eraseFunctionGraphics();

            arrayFunCalcThread = new Thread(new SortingTask(arrayFunction, numberOfLists));
            linFunCalcThread = new Thread(new LinearFunctionCalcTask(linearFunction));

            arrayFunCalcThread.setDaemon(true);
            linFunCalcThread.setDaemon(true);

            arrayFunCalcThread.start();
            linFunCalcThread.start();
        }
    }

    public void incrementGraphicScale() {
        graphicCanvas.incrementScale();
    }

    public void decrementGraphicScale() {
        graphicCanvas.decrementScale();
    }

    public void stopGraphicBuilding() {
        if (!arrayFunCalcThread.isInterrupted()) {
            arrayFunCalcThread.interrupt();
            linFunCalcThread.interrupt();
        }
    }
}
